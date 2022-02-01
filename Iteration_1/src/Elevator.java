import java.util.*;

public class Elevator implements Runnable {
    private int elevDoorNum;
    private Boolean floorLamps[];
    private Scheduler scheduler;
    private int currentFloor;
    private int maxFloors;
    private Direction direction;
    private List<Integer> localQueue;

    public Elevator(int id, Scheduler sch, int floorCount) {
    	this.elevDoorNum = id;
    	this.currentFloor = 1;
    	this.scheduler = sch;
    	floorLamps = new Boolean[floorCount];
    	maxFloors = floorCount;
    }

    @Override
    public void run() {
    	while(true) {
    		moveElevator();
    	}
    }

    public void floorSelectPanel(int selFloor) {
    	this.scheduler.addToServiceQueue(selFloor, this.elevDoorNum);
    	floorLamps[selFloor - 1] = true;
    }

    public void moveElevator() {
    	if(!this.scheduler.checkQueue(this.elevDoorNum)) {
    		try {
    			wait();
    		} catch(InterruptedException e) {
    			System.err.println(e);
    		}
    	}
    	int destination = this.scheduler.getFloorNum(this.elevDoorNum);
    	if((destination - currentFloor) > 0) {
    		direction = Direction.UP;
    	} else if((destination - currentFloor) < 0) {
    		direction = Direction.DOWN;
    	} else {
    		// prompt user to choose new floor from panel
    	}
    	if(direction == Direction.UP) {
    		while(!(currentFloor == destination)) {
    			simMovement();
    			currentFloor++;
    			if(this.scheduler.floorInQueue(currentFloor)) {
    				openDoor();
    				floorSelectPanel(selectedFloor); // selectedFloor is user selected floor destination
    				closeDoor();
    			}
    		}
    	} else if(direction == Direction.DOWN) {
    		while(!(currentFloor == destination)) {
    			simMovement();
    			currentFloor--;
    			//check if current floor is in the queue
    		}
    	}
    	//once its at its destination
    	floorLamps[destination - 1] = false;
    	openDoor();
    	closeDoor();
    }

    public void simMovement() {
    	try {
    		Thread.sleep(10); // example of 10 ms
    	} catch(InterruptedException e) {
    		System.err.println(e);
    	}
    }

    public int getCurrentFloor() {
        return currentFloor;
    }

    private void openDoor() {

    }

    private void closeDoor() {

    }
}
