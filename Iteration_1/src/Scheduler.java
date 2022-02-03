/**
 * Scheduler class
 *
 */
import java.util.ArrayList;
import java.util.List;

public class Scheduler {
	private List<List<Boolean>> queues;
	//private List<List<Integer>> queues;
	private List<Boolean> elevator;
	
	private boolean available;
	//constructor to initialize variables
	public Scheduler() {
		queues = new ArrayList<List<Boolean>>();
		//queues = new ArrayList<List<Integer>>();		
		elevator = new ArrayList<Boolean>();	
		available = true;
	}
	// add floor number to elevator queue and notify once queued. 
	public synchronized void addToServiceQueue (int floorNum, int elevID) {		
		//List<Integer> elevator = new ArrayList<Integer>();
		elevator.add(floorNum, true);
		queues.add(elevID, elevator);	
		notifyAll();
	}
	// return next floor number given elevator id
	public int getFloorNum(int elevID) {
		
        return queues.get(elevID).indexOf(true);
    }
	
	// check if a given floor number is in a specific elevator's queue
	public boolean floorInQueue(int floorNum, int elevID) {		
        return queues.get(elevID).get(floorNum);
    }
    // remove floor number once elevator reaches destination
	public void atDestination(int floorNum, int elevID) {
		removeFromQueue(floorNum, elevID);
	}
	// check specific elevator queue for any floor number
    public synchronized boolean checkQueue(int elevID) {
        if (queues.get(elevID).isEmpty()) {
        	return true;
        } 
        notifyAll();
        return false;
    }
    // return the highest floor of the building
    public int maxQueueFloor() {
        return queues.get(0).size();
    }
    // is it necessary for this one?
    public int minQueueFloor() {
        return 1;
    }
    // remove the floor number from the elevator queue: set to false.
    public void removeFromQueue(int floorNum, int elevID) {
    	queues.get(elevID).set(floorNum, false);
    }	
    // display to check resulted matrix:
    public void printQueue() {
    	//for (int i = 0; i < queues.size(); i++) {
    	 //   for (int j = 0; j < queues.get(i).size(); j++) {    		    	
    	 //		System.out.print(queues.get(i).get(j));
    	//	}
    	//	System.out.println();
    //	}
    	System.out.print(queues);
    }

}
