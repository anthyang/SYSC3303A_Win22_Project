/**
 * Scheduler class to coordinate the elevator and floor queues.
 *
 */
import java.util.*;

public class Scheduler {
	// store requests by floor and direction:
	private List<Map<Direction, List<Request>>> floors;
	// store requests in a long list -- linked to floors
	private List<List<Request>> queues;

	//constructor to initialize queues, list, request.
	public Scheduler() {
		floors = new ArrayList<>();
		queues = new ArrayList<>();
		for (int i = 0; i < Building.NUM_FLOORS; i++) {
			Map<Direction, List<Request>> directionMap = new HashMap<>();
			List<Request> upQueue = new ArrayList<>();
			List<Request> downQueue = new ArrayList<>();
			directionMap.put(Direction.UP, upQueue);
			directionMap.put(Direction.DOWN, downQueue);
			floors.add(directionMap);

			queues.add(upQueue);
			queues.add(downQueue);
		}
	}

	private List<Request> getQueue(int floor, Direction direction) {
		return floors.get(floor - 1).get(direction);
	}

	// addition of source floor, destination floor, and direction to queue.
	public synchronized void addToServiceQueue(int sourceFloor, int destFloor, Direction direction) {
		Request req = new Request(sourceFloor, destFloor, direction);
		getQueue(sourceFloor, direction).add(req);

		notifyAll();
	}
	
	// check specific request queue given any floor number and direction
	public List<Request> checkRequest(int floorNum, Direction direction) {
		List<Request> aRequest = new ArrayList<>();
		List<Request> floorQueue = getQueue(floorNum, direction);
		if (floorQueue.isEmpty()) {
			System.out.println("No request in the direction " + direction + " from the given floor " + floorNum);
		} else {
			aRequest = new ArrayList<>(floorQueue);
			floorQueue.clear();
		}

		return aRequest;
    }

	private boolean allQueuesEmpty() {
		return queues.stream().allMatch(List::isEmpty);
	}
    
	// return a request if there is a request in queue
	public synchronized List<Request> getAvailRequest() {
		while (allQueuesEmpty()) {
			try {
				wait();
			} catch (InterruptedException e) {
				System.out.println("An interrupted exception error occurs.");
			}
		}

		List<Request> availRequests = new ArrayList<>();
		for (List<Request> queue : queues) {
			if (!queue.isEmpty()) {
				availRequests = new ArrayList<>(queue);
				queue.clear();
				return availRequests;
			}
		}
		return availRequests;
	}
    
    // display to check resulted matrix:
    public void printQueue() {
    	for (List<Request> list : queues) {
			for (Request request : list) {
				System.out.println(request);
			}
    	}
    }

}

