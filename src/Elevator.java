import java.net.DatagramPacket;
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
	private int sendPort;
	private Timer timer;
	private TimerTask timerTask;
	private int openDoorTime = Config.DOOR_MOVEMENT;
	private boolean active;

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
		this.sendPort = Scheduler.ELEVATOR_UPDATE_PORT;
		timer = new Timer("Timer "+id);//Making a new timer for each elevator
		this.active = true;
    }

	/**
	 * Additional Elevator constructor to override the default send port.
	 * @param id represents an Integer of the elevator's identification number.
	 * @param floorCount represents an Integer of the maximum amount of floors in the building.
	 */
	public Elevator(int id, int floorCount, int sendPort) {
		super("Elevator " + id);
		this.elevDoorNum = id;
		this.currentFloor = 1;
		this.floorLamps = new boolean[floorCount];
		this.dirLamps = 0; // -1 for down, 0 - not moving, 1 for up:
		this.doorsOpen = true;
		this.sendPort = sendPort;
		timer = new Timer("Timer "+id);//Making a new timer for each elevator

		this.active = true;
		this.setTimeout(Config.TIMEOUT);
	}

    /**
     * Picks up and drops off passengers endlessly
     */
    public void run() {
		while (this.active) {
			if (!this.doorsOpen) {
				// Ensure passengers can load/unload
				this.openDoor();
			}
			// Ask scheduler for next direction of travel
			this.direction = this.serveNewRequest();
			if (this.direction == null) {
				// Null direction indicates hard fault
				return;
			}

			if (this.direction == Direction.NOT_MOVING) {
				// Notify the scheduler that the passengers have been picked up
				this.notifyArrival(false);
			} else {
				this.closeDoor();
				do {
					// Move elevator up/down until the scheduler tells it to stop and open its doors
					this.simMovement();
				} while (!this.notifyArrival(true));
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
				this.sendPort
		);

		if (response == null) {
			// rpcCall could not receive data from scheduler, shut down
			this.active = false;
			return null;
		}

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
		this.doorsOpen = true;
		timerTask = new TimerTask() {
			/**
			 * Timertask increase the counter by 1 every 1000ms, and this is controlled by the timer
			 */
			@Override
			public void run() {
					/* When counter reach 10, which means the door of the elevator has been open for 10 seconds
					without closing, timer task will force elevator to close the door, cancel the timer while also
					reset the counter to 0
					 */
					closeDoor();
					openDoorTime = Config.DOOR_MOVEMENT;
			}
		};
		timer.schedule(timerTask, 10000);
    	try {
    		Thread.sleep(Config.DOOR_MOVEMENT);
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
		timerTask.cancel();
		try {
    		Thread.sleep(Config.DOOR_MOVEMENT);
    	} catch (InterruptedException e) {
    		e.printStackTrace();
    	}
    }

	/**
	 * Notify the scheduler of arrival at a floor
	 * @return true if the elevator is stopping, false otherwise
	 */
	public boolean notifyArrival(boolean wasMoving) {
		ElevatorReport report = new ElevatorReport(this.elevDoorNum, this.direction, this.currentFloor);
        this.log("Notifying scheduler of arrival at floor " + this.currentFloor + " at "
				+ InetAddress.getLoopbackAddress() + ":" + this.sendPort);

        byte[] msg = Host.serialize(report);

		DatagramPacket response = this.rpcCall(
				msg,
				InetAddress.getLoopbackAddress(),
				this.sendPort
		);

		if (response == null) {
			// rpcCall could not receive data from scheduler, shut down
			this.active = false;
			return true;
		}

		byte[] responseData = response.getData();
		if (response.getLength() != 1 || (responseData[0] != 0 && responseData[0] != 1 && responseData[0] != 2)) {
			throw new RuntimeException("Invalid response from Scheduler");
		}

		if (wasMoving) {
			// Log messages to say we're stopping
			if (responseData[0] == 1) {
				this.log("Stopping at floor " + this.currentFloor);
				return true;
			}else if(responseData[0] == 2){
				openDoorTime = Config.ERROR_DOOR_MOVEMENT;
			} else {
				this.log("Nothing to service at floor " + this.currentFloor + ", continuing.");
				return false;
			}
		}

		return true;
	}

	public static void main(String[] args) {
		for (int i = 1; i <= Config.NUMBER_OF_ELEVATORS; i++) {
			Elevator elevator = new Elevator(i, Config.NUMBER_OF_FLOORS);
			Thread elevatorSystem = new Thread(elevator);
			elevatorSystem.start();
		}
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
}
