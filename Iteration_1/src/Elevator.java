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
    
    private final int ELEVATOR_MOVEMENT = 2832;
    private final int DOOR_MOVEMENT = 4590;

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
    	floorLamps = new Boolean[floorCount];
    	floorsToVisit = new HashSet<>();
    }

    /**
     * Runs the moveElevator() method endlessly.
     */
    public void run() {
    	while(true) {
    		moveElevator();
    	}
    }
    
    /**
     * Simulates the movement of the elevator between each floor and updates the currentFloor variable.
     * @param dir represents Direction enumerator of the elevator.
     */
    public void simMovement(Direction dir) {
    	System.out.println("Elevator " + this.elevDoorNum + " is moving.");
		System.out.println("Current schedule: \n");
		System.out.println("Floor to visit: " +this.floorsToVisit+"\n");
    	try {
    		Thread.sleep(ELEVATOR_MOVEMENT);
    	} catch(InterruptedException e) {
    		System.err.println(e);
    	}
    	if(dir == Direction.UP) {
			currentFloor++;
			System.out.println("Elevator " + this.elevDoorNum + " is at floor "+this.currentFloor);
		} else {
			currentFloor--;
			System.out.println("Elevator " + this.elevDoorNum + " is at floor "+this.currentFloor);
		}
    	
    }

    
    /**
     * Simulates the doors of the elevator opening given the DOOR_MOVEMENT constant which
     * is calculated to be half the average unloading/loading time.
     */
    private void openDoor() {
    	System.out.println("Elevator " + this.elevDoorNum + " is opening doors at floor " + this.currentFloor);
    	try {
    		Thread.sleep(DOOR_MOVEMENT);
    	} catch(InterruptedException e) {
    		System.err.println(e);
    	}
    }

    /**
     * Simulates the doors of the elevator closing given the DOOR_MOVEMENT constant which
     * is calculated to be half the average unloading/loading time.
     */
    private void closeDoor() {
    	System.out.println("Elevator " + this.elevDoorNum + " is closing doors at floor " + this.currentFloor);
		System.out.println("Upcoming schedule:\n" );
		scheduler.printQueue();
		try {
    		Thread.sleep(DOOR_MOVEMENT);
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
    	openDoor();
		List<Request> req = this.scheduler.getAvailRequest();
    	closeDoor();

		int sourceFloor = 1;
		Direction direction = Direction.NOT_MOVING;
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
    		List<Request> reqList = this.scheduler.checkRequest(currentFloor, direction);
    		for(Request r : reqList) {
    			floorsToVisit.add(r.getDestFloor());
    			floorLamps[r.getDestFloor() - 1] = true;
    		}
    		if((!reqList.isEmpty()) || (floorsToVisit.contains(currentFloor))) {
    			openDoor();
    			floorsToVisit.remove(currentFloor);
    			floorLamps[currentFloor - 1] = false;
    			closeDoor();
    		}
    	}
    }

	public int getCurrentFloor() {return currentFloor;}

	public Set<Integer> getFloorToVisit() {return floorsToVisit;}
}
