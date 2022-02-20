import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Elevator subsystem class receives information packets from the scheduler to control the elevator motors and to open the doors.
 * The elevator also monitors and decides on destination requests while updating the lamps.
 * @author Gilles Myny
 * @id 101145477
 */
public class Elevator implements Runnable {
    private int elevDoorNum;
    private Boolean floorLamps[];
    private Scheduler scheduler;
    private int currentFloor;
    private Set<Integer> floorsToVisit;
	private List<Request> req;
	private Direction direction;
        
    // add direction lamps to donate arrival and direction of an elevator at a floor
    private int dirLamps;
    
    // add datagram for notifying scheduler
   // private DatagramPacket sendPacket, receivePacket;
   // private DatagramSocket sendSocket;
   // private int port = 5000;

    /**
     * The Elevator constructor initializes all necessary variables.
     * @param id represents an Integer of the elevator's identification number.
     * @param sch represents a Scheduler object.
     * @param floorCount represents an Integer of the maximum amount of floors in the building.
     */
    public Elevator(int id, Scheduler sch, int floorCount) {
    	this.elevDoorNum = id;
    	this.currentFloor = 1;
    	this.scheduler = sch;
		sch.registerElevator(this);
    	floorLamps = new Boolean[floorCount];
    	floorsToVisit = new HashSet<>();
    	// -1 for down, 0 - not moving, 1 for up:
    	dirLamps = 0;     	
    	
    	// for iteration #3
      /*  try {
        	sendSocket = new DatagramSocket();
         } catch (SocketException se) {
            se.printStackTrace();
            System.exit(1);
         } */

    }

    /**
     * Runs the moveElevator() method endlessly.
     */
    public void run() {
		openDoor();
		req = this.scheduler.updateQueue(this, this.currentFloor);
		while (req != null) {
    		moveElevator();
			req = this.scheduler.updateQueue(this, this.currentFloor);
    	}
    }
    
    /**
     * Simulates the movement of the elevator between each floor and updates the currentFloor variable.
     * @param dir represents Direction enumerator of the elevator.
     */
    public void simMovement(Direction dir) {
    	System.out.println(getTimestamp() + " Elevator " + this.elevDoorNum + " is moving.");
    	try {
    		Thread.sleep(Config.ELEVATOR_MOVEMENT);
    	} catch(InterruptedException e) {
    		System.err.println(e);
    	}
    	if(dir == Direction.UP) {
			currentFloor++;
			System.out.println(getTimestamp() + " Elevator " + this.elevDoorNum + " is at floor "+this.currentFloor);
			dirLamps = 1;
		} else {
			currentFloor--;
			System.out.println(getTimestamp() + " Elevator " + this.elevDoorNum + " is at floor "+this.currentFloor);
			dirLamps = -1;
		}    	
    	
    }
    
    /**
     * Simulates the doors of the elevator opening given the DOOR_MOVEMENT constant which
     * is calculated to be half the average unloading/loading time.
     */
    private void openDoor() {    	
    	if (dirLamps != 0) {
    		// notify scheduler if an elevator arrived at a floor;
    		notifyArrival();
    		// turn directon lamp off once arrival is completed.
    		dirLamps = 0;
    	}
    	System.out.println(getTimestamp() + " Elevator " + this.elevDoorNum + " is opening doors at floor " + this.currentFloor);
    	try {
    		Thread.sleep(Config.DOOR_MOVEMENT);
    	} catch(InterruptedException e) {
    		System.err.println(e);
    	}
    }

	/**
	 * Get a string representation of the current time
	 * @return a timestamp
	 */
	private String getTimestamp() {
		return Timestamp.from(ZonedDateTime.now().toInstant().truncatedTo(ChronoUnit.SECONDS)).toString();
	}

    /**
     * Simulates the doors of the elevator closing given the DOOR_MOVEMENT constant which
     * is calculated to be half the average unloading/loading time.
     */
    private void closeDoor() {
    	System.out.println(getTimestamp() + " Elevator " + this.elevDoorNum + " is closing doors at floor " + this.currentFloor);
		try {
    		Thread.sleep(Config.DOOR_MOVEMENT);
    	} catch(InterruptedException e) {
    		System.err.println(e);
    	}
    }

    /**
     * Handles the elevator's functionality, ranging from commanding the doors to open
     * and close, to moving the elevator to specific floors given information packets
     * called Request object's from the scheduler.
     */
    public void moveElevator() {
    	closeDoor();

		int sourceFloor = 1;
		direction = Direction.NOT_MOVING;
		for (Request request : req) {
			sourceFloor = request.getSourceFloor();
			floorsToVisit.add(request.getDestFloor());
			floorLamps[request.getDestFloor() - 1] = true;
			direction = request.getDirection();			
		}
    	
    	while(!(currentFloor == sourceFloor)) {    		
			simMovement((sourceFloor > currentFloor) ? Direction.UP : Direction.DOWN);
		}
    	
    	while(!floorsToVisit.isEmpty()) {
    		simMovement(direction);
			req = scheduler.updateQueue(this, currentFloor);
    		for(Request r : req) {
    			floorsToVisit.add(r.getDestFloor());
    			floorLamps[r.getDestFloor() - 1] = true;
    		}
    		if((reqAtCurrFloor()) || (floorsToVisit.contains(currentFloor))) {
    			openDoor();
    			floorsToVisit.remove(currentFloor);
    			floorLamps[currentFloor - 1] = false;
				if (!floorsToVisit.isEmpty()) {
					closeDoor();
				}
    		}
    	}
    }

	/**
	 * Check whether there is a pending request at the current floor
	 * @return true if a request is pending, false otherwise
	 */
	private boolean reqAtCurrFloor() {
		return req.stream().anyMatch(request -> request.getSourceFloor() == currentFloor);
	}

	/**
	 * Notify the scheduler of arrival at a floor
	 */
	public void notifyArrival() {
    	this.scheduler.serviceComplete(this, this.currentFloor);
    	
    	/* for iteration #3
        String s = "arrived at floor " + currentFloor;
        System.out.println("Elevator: notifying scheduler floor arrival");

        byte msg[] = s.getBytes();

       try {
           sendPacket = new DatagramPacket(msg, msg.length,
                                           InetAddress.getLocalHost(), port);
        } catch (UnknownHostException e) {
           e.printStackTrace();
           System.exit(1);
        }

        System.out.println("elevator: notifying:");
        System.out.println("scheduler: " + sendPacket.getAddress());
        System.out.println("scheduler port: " + sendPacket.getPort());
        System.out.print("with data: ");
        System.out.println(new String(sendPacket.getData()));

        try {
           sendSocket.send(sendPacket);
        } catch (IOException e) {
           e.printStackTrace();
           System.exit(1);
        }

        System.out.println("Elevator: notified Scheduler.\n"); */
  	
    }

	/**
	 * Get the elevator's direction
	 * @return the current direction
	 */
	public Direction getDirection() {
		return direction;
	}

	/**
	 * Get the elevator's current floor
	 * @return the current floor
	 */
	public int getCurrentFloor() {return currentFloor;}

	/**
	 * Get the set of floors to visit
	 * @return the floors to visit
	 */
	public Set<Integer> getFloorToVisit() {
		return floorsToVisit;
	}
}
