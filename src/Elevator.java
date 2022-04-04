import java.net.DatagramPacket;
import java.lang.Math;
import java.net.InetAddress;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Elevator subsystem class receives information packets from the scheduler to control the elevator motors and to open the doors.
 * The elevator also monitors and decides on destination requests while updating the lamps.
 * @author Gilles Myny
 * @id 101145477
 */
public class Elevator extends Host implements Runnable {
    private int elevDoorNum;
    private boolean[] floorLamps;
    private int currentFloor;
	private Direction direction;
	private boolean doorsOpen;
	private Timer timer;
	private TimerTask timerTask;
	private StopWatch serviceTimer = new StopWatch();
	private StopWatch loadTimer = new StopWatch();
	private int openDoorTime = Config.DOOR_MOVEMENT;

    // add direction lamps to donate arrival and direction of an elevator at a floor
    private int dirLamps;

    /**
     * The Elevator constructor initializes all necessary variables.
     * @param id represents an Integer of the elevator's identification number.
     * @param floorCount represents an Integer of the maximum amount of floors in the building.
     */
    public Elevator(int id, int floorCount) {
		super("Elevator " + id);
    	this.elevDoorNum = id;
    	this.currentFloor = 1;
    	this.floorLamps = new boolean[floorCount];
    	this.dirLamps = 0; // -1 for down, 0 - not moving, 1 for up:
		this.doorsOpen = true;
		timer = new Timer("Timer "+id);//Making a new timer for each elevator
		timerTask = new TimerTask() {
			public void run() {}
		};
    }

    /**
     * Picks up and drops off passengers endlessly
     */
    public void run() {
		while (!Thread.interrupted()) {
			if (!this.doorsOpen) {
				// Ensure passengers can load/unload
				this.openDoor();
			}
			this.log("Loading/Unloading Passengers");
			loadTimer.start();
			// Ask scheduler for next direction of travel
			this.direction = this.serveNewRequest();

			if (this.direction == Direction.NOT_MOVING) {
				// Notify the scheduler that the passengers have been picked up
				this.notifyArrival(false);
			} else {
				this.closeDoor();
				this.log("Loading/unloading time was " + (loadTimer.end() / Math.pow(10, 6)) + " msec");
				loadTimer.reset();
				do {
					// Move elevator up/down until the scheduler tells it to stop and open its doors
					serviceTimer.start();
					this.simMovement();
				} while (!this.notifyArrival(true));
				serviceTimer.end();
				this.log("Servicing time was " + (serviceTimer.end() / Math.pow(10, 6)) + " msec");
				serviceTimer.reset();
			}
		}
    }

	/**
	 * Get a new direction of travel from the scheduler
	 * @return the direction the elevator should travel
	 */
	public Direction serveNewRequest() {
		byte[] id = { (byte)this.elevDoorNum };
		// Response should be an array of one byte with -1, 0, or 1
		DatagramPacket response = this.rpcCall(
				id,
				InetAddress.getLoopbackAddress(),
				Scheduler.ELEVATOR_UPDATE_PORT
		);

		byte[] responseData = response.getData();
		if (response.getLength() != 1 || responseData[0] < -1 || responseData[0] > 1) {
			throw new RuntimeException("Invalid response from Scheduler");
		}

		this.log("Received request from scheduler at " + response.getAddress() + ":" + response.getPort());
		switch (responseData[0]) {
			case -1:
				return Direction.DOWN;
			case 0:
				return Direction.NOT_MOVING;
			default:
				return Direction.UP;
		}
	}
    
    /**
     * Simulates the movement of the elevator between each floor and updates the currentFloor variable.
     */
    public void simMovement() {
		this.log("Moving " + direction.getReadable() + ".");
    	try {
    		Thread.sleep(Config.ELEVATOR_MOVEMENT);
    	} catch (InterruptedException e) {
    		e.printStackTrace();
    	}

    	if (this.direction == Direction.UP) {
			currentFloor++;
			dirLamps = 1;
		} else {
			currentFloor--;
			dirLamps = -1;
		}
    }
    
    /**
     * Simulates the doors of the elevator opening given the DOOR_MOVEMENT constant which
     * is calculated to be half the average unloading/loading time.
     */
    private void openDoor() {
		this.log("Opening doors at floor " + this.currentFloor);
		timerTask = new TimerTask() {
			/**
			 * Timertask Trigger open door once the timerTask thread start running
			 */
			@Override
			public void run() {
				log("Detected transient fault.");

				try {
					Thread.sleep(2000);
					openDoorTime = Config.DOOR_MOVEMENT;
					Thread.sleep(openDoorTime);
					log("Door opened at floor "+currentFloor);
					doorsOpen = true;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		timer.schedule(timerTask, Config.TIMER_WAIT);
    	try {
    		Thread.sleep(openDoorTime);
			this.doorsOpen = true;
			timerTask.cancel();
    	} catch (InterruptedException e) {
    		e.printStackTrace();
    	}
    }



    /**
     * Simulates the doors of the elevator closing given the DOOR_MOVEMENT constant which
     * is calculated to be half the average unloading/loading time.
     */
    private void closeDoor() {
    	this.log("Closing doors at floor " + this.currentFloor);
		this.doorsOpen = false;
		try {
    		Thread.sleep(Config.DOOR_MOVEMENT);
    	} catch (InterruptedException e) {
    		e.printStackTrace();
    	}
		timerTask.cancel();
    }

	/**
	 * Notify the scheduler of arrival at a floor
	 * @return true if the elevator is stopping, false otherwise
	 */
	public boolean notifyArrival(boolean wasMoving) {
		ElevatorReport report = new ElevatorReport(this.elevDoorNum, this.direction, this.currentFloor);
        this.log("Notifying scheduler of arrival at floor " + this.currentFloor + " at "
				+ InetAddress.getLoopbackAddress() + ":" + Scheduler.ELEVATOR_UPDATE_PORT);

        byte[] msg = Host.serialize(report);

		DatagramPacket response = this.rpcCall(
				msg,
				InetAddress.getLoopbackAddress(),
				Scheduler.ELEVATOR_UPDATE_PORT
		);

		byte[] responseData = response.getData();

		if (response.getLength() != 1 || !(responseData[0] >= 0 && responseData[0] <= 3)) {
			log(responseData[0] + "");
			throw new RuntimeException("Invalid response from Scheduler");
		}

		if (wasMoving) {
			// Log messages to say we're stopping
			if (responseData[0] > 0) {
				this.log("Stopping at floor " + this.currentFloor);
				if (responseData[0] == 2) {
					openDoorTime = Config.ERROR_DOOR_MOVEMENT;
				}else if (responseData[0] == 3){
					this.log("Detect hard fault. Trigger hard fault solution for Elevator "+elevDoorNum);
					Thread.currentThread().interrupt();
				}
				return true;
			} else {
				this.log("Nothing to service at floor " + this.currentFloor + ", continuing.");
				return false;
			}
		}

		return true;
	}

	/**
	 * Get the elevator's direction of travel
	 * @return the elevator's direction
	 */
	public Direction getDirection() {
		return direction;
	}

	/**
	 * Update the elevator's direction
	 * @param d the elevator's next direction of travel
	 */
	public void setElevatorDirection(Direction d) {
		this.direction = d;
	}

	/**
	 * Get the elevator's current floor
	 * @return the elevator's current floor
	 */
	public int getCurrentFloor() {
		return currentFloor;
	}

	public static void main(String[] args) {
		for (int i = 1; i <= Config.NUMBER_OF_ELEVATORS; i++) {
			Elevator elevator = new Elevator(i, Config.NUMBER_OF_FLOORS);
			Thread elevatorSystem = new Thread(elevator);
			elevatorSystem.start();
		}
	}
}
