/**
 * Scheduler class to coordinate the elevator and floor queues.
 *
 */
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

public class Scheduler {
	// a queue to store the floor and requests:
	private LinkedList<List<Request>> queues; 
	// a list to store multiple requests:
	private List<Request> r;
	// set the highest floor of a building
	private int maxFloor;
	// handle an elevator request in the format: floor request, direction (UP, DOWN)
	private Request req;
	
	// handle an elevator direction specification. 
	private Direction direction;
	// define the enumerator for the elevator directions
	private enum Direction{UP,DOWN};
	// boolean of the request queue
	private boolean empty;
	//constructor to initialize queues, list, request.
	public Scheduler() {
		queues = new LinkedList<>();
		r = new ArrayList<Request>();
		empty = true;
	}
	// synchronized the addition of source floor, destination floor, and direction to queue. 
	public synchronized void addToServiceQueue (int sourceFloor, int destFloor, String direction) {		
        while (!empty) {
            try {
                wait();
            } catch (InterruptedException e) {
            	System.out.println("An interrupted exception error occurs.");
                return;
            }
        }
        
        req = new Request(1, 4, direction);       
        r.add(req);
        // add to queue.
        queues.add(r);
        empty = false;
		notifyAll();
	}
	
	// check specific request queue given any floor number and direction
    public List<Request> checkRequest(int floorNum, String direction) {
        if (queues.get(floorNum).isEmpty()) {
        	System.out.println("No request in the direction " + direction + " from the given floor " + floorNum);
        	return null;
        } 
        
       List aRequest = null;
       for (int i = queues.get(floorNum).size(); i > 0; i--)
        {
        	 
        	 if ((queues.get(floorNum).contains(direction)))
        	 {
        		 aRequest = queues.get(floorNum);
        		 queues.remove(floorNum);
        		 break;
        	 }
        	
        }
         return aRequest;        
    }
    
    // return a request if there is a request in queue,
    // it returns null 
    public synchronized List<Request> getAvailRequest() {
        while (empty) {
            try {
                wait();
            } catch (InterruptedException e) {
            	System.out.println("An interrupted exception error occurs.");
                
            }
        }
        empty = true;
        // send a notification to wake up all threads.
        notifyAll();
    	return queues.pollLast();
    }
    
    // display to check resulted matrix:
    public void printQueue() {
    	for (List list : queues) 
    	{
    		   		
    		for (int i = 0; i < list.size(); i++)
    		{
    			System.out.println(list.get(i));
    		}
    	}
    }

}

