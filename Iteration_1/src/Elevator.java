import java.util.*;

public class Elevator implements Runnable {
    private int elevDoorNum;
    private Boolean floorLamps[];
    private Scheduler scheduler;
    private int currentFloor;
    private Set<Integer> floorsToVisit; 
    
    private final int ELEVATOR_MOVEMENT = 2832;
    private final int DOOR_MOVEMENT = 4590;

    public Elevator(int id, Scheduler sch, int floorCount) {
    	this.elevDoorNum = id;
    	this.currentFloor = 1;
    	this.scheduler = sch;
    	floorLamps = new Boolean[floorCount];
    	floorsToVisit = new HashSet<>();
    }

    public void run() {
    	while(true) {
    		moveElevator();
    	}
    }
    
    public void simMovement(Direction dir) {
    	System.out.println("Elevator " + this.elevDoorNum + " is moving.");
    	try {
    		Thread.sleep(ELEVATOR_MOVEMENT);
    	} catch(InterruptedException e) {
    		System.err.println(e);
    	}
    	if(dir == Direction.UP) {
			currentFloor++;
		} else {
			currentFloor--;
		}
    	
    }

    private void openDoor() {
    	System.out.println("Elevator " + this.elevDoorNum + " is opening doors at floor " + this.currentFloor);
    	try {
    		Thread.sleep(DOOR_MOVEMENT);
    	} catch(InterruptedException e) {
    		System.err.println(e);
    	}
    }

    private void closeDoor() {
    	System.out.println("Elevator " + this.elevDoorNum + " is closing doors at floor " + this.currentFloor);
    	try {
    		Thread.sleep(DOOR_MOVEMENT);
    	} catch(InterruptedException e) {
    		System.err.println(e);
    	}
    }

    public void moveElevator() {
    	openDoor();
    	Request req = this.scheduler.getAvailRequest();
    	closeDoor();
    	
    	int sourceFloor = req.sourceFloor;
    	
    	while(!(currentFloor == sourceFloor)) {
			simMovement((sourceFloor > currentFloor) ? Direction.UP : Direction.DOWN);
		}
    	
    	floorsToVisit.add(req.destFloor);
    	floorLamps[req.destFloor - 1] = true;
    	Direction direction = req.direction;
    	
    	while(!floorsToVisit.isEmpty()) {
    		simMovement(direction);
    		List<Request> reqList = this.scheduler.checkRequest(currentFloor, direction);
    		for(Request r : reqList) {
    			floorsToVisit.add(r.destFloor);
    			floorLamps[r.destFloor - 1] = true;
    		}
    		if((!reqList.isEmpty()) || (floorsToVisit.contains(currentFloor))) {
    			openDoor();
    			floorsToVisit.remove(currentFloor);
    			floorLamps[currentFloor - 1] = false;
    			closeDoor();
    		}
    	}
    }
}
