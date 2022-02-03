import java.util.*;

public class Elevator implements Runnable {
    private int elevDoorNum;
    private Boolean floorLamps[];
    private Scheduler scheduler;
    private int currentFloor;
    private Direction direction;
    
    private final int ELEVATOR_MOVEMENT = 2832;
    private final int DOOR_MOVEMENT = 4590;

    public Elevator(int id, Scheduler sch, int floorCount) {
    	this.elevDoorNum = id;
    	this.currentFloor = 1;
    	this.scheduler = sch;
    	floorLamps = new Boolean[floorCount];
    }

    public void run() {
    	while(true) {
    		moveElevator();
    	}
    }

    public void floorSelectPanel(int selFloor) {
    	this.scheduler.addToServiceQueue(selFloor, this.elevDoorNum);
    	floorLamps[selFloor - 1] = true;
    }
    
    public void simMovement() {
    	System.out.println("Elevator " + this.elevDoorNum + " is moving.");
    	try {
    		Thread.sleep(ELEVATOR_MOVEMENT); // time in ms to move from one floor to another
    	} catch(InterruptedException e) {
    		System.err.println(e);
    	}
    }

    private void openDoor() {
    	System.out.println("Elevator " + this.elevDoorNum + " is opening doors at floor " + this.currentFloor);
    	try {
    		Thread.sleep(DOOR_MOVEMENT); // time in ms to open doors
    	} catch(InterruptedException e) {
    		System.err.println(e);
    	}
    }

    private void closeDoor() {
    	System.out.println("Elevator " + this.elevDoorNum + " is closing doors at floor " + this.currentFloor);
    	try {
    		Thread.sleep(DOOR_MOVEMENT); // time in ms to close doors
    	} catch(InterruptedException e) {
    		System.err.println(e);
    	}
    }

    public void moveElevator() {
    	if(this.scheduler.checkQueue(this.elevDoorNum)) {
    		try {
    			wait();
    		} catch(InterruptedException e) {
    			System.err.println(e);
    		}
    	}
    	int destination = this.scheduler.getFloorNum(this.elevDoorNum); // need to implement getFloorNum in scheduler to return floor integer of next floor in queue
    	direction = this.scheduler.getDirection(this.elevDoorNum);
    	if(direction == Direction.UP) {
    		while(!(currentFloor == destination)) {
    			simMovement();
    			currentFloor++;
    			if(this.scheduler.floorInQueue(currentFloor, this.elevDoorNum)) { // need to implement floorInQueue in scheduler to return boolean if current floor is in the queue
    				openDoor();
    				floorSelectPanel(selectedFloor); // selectedFloor is user selected floor destination
    				closeDoor();
    			}
    		}
    	} else if(direction == Direction.DOWN) {
    		while(!(currentFloor == destination)) {
    			simMovement();
    			currentFloor--;
    			if(this.scheduler.floorInQueue(currentFloor, this.elevDoorNum)) { // need to implement floorInQueue in scheduler to return boolean if current floor is in the queue
    				openDoor();
    				floorSelectPanel(selectedFloor); // selectedFloor is user prompt selected floor destination
    				closeDoor();
    			}
    		}
    	}
    	//once its at its destination
    	floorLamps[destination - 1] = false;
    	this.scheduler.atDestination(destination, this.elevDoorNum); // need to implement atDestination in scheduler to remove given floor number from queue
    	openDoor();
    	closeDoor();
    }
}
