import java.util.*;
import java.util.stream.Collectors;

/**
 * The scheduler class
 */
public class Scheduler {

	private Queue<Request> masterQueue;
	private Map<Elevator, List<Request>> elevQueueMap;
	private boolean doneReceiving;

	public static int ELEVATOR_UPDATE_PORT = 5000;
	public static int NEW_REQUEST_PORT = 5001;

	/**
	 * Scheduler constructor
	 */
	public Scheduler() {
		masterQueue = new LinkedList<>();
		elevQueueMap = new HashMap<>(Config.NUMBER_OF_ELEVATORS);
		doneReceiving = false;
	}

	/**
	 * Add a request to be serviced
	 * @param request the request to add
	 */
	public synchronized void addExternalRequest(Request request) {
		masterQueue.add(request);
		notifyAll();
	}

	/**
	 * Register an elevator with the scheduler
	 * @param e the elevator to register
	 */
	public void registerElevator(Elevator e) {
		elevQueueMap.put(e, new ArrayList<>());
	}

	/**
	 * Mark service complete by an elevator
	 * @param e the elevator that has serviced the floor
	 * @param destFloor the floor the elevator has serviced
	 */
	public void serviceComplete(Elevator e, int destFloor) {
		elevQueueMap.get(e).removeIf(request -> request.getDestFloor() == destFloor);
	}

	/**
	 * Get an up to date version of the elevator's queue
	 * @param e the elevator
	 * @param currentFloor the elevator's current floor
	 * @return the elevator's queue
	 */
	public synchronized List<Request> updateQueue(Elevator e, int currentFloor) {
		if (doneReceiving && masterQueue.isEmpty() && elevQueueMap.get(e).isEmpty()) {
			return null;
		}

		while (masterQueue.isEmpty() && elevQueueMap.isEmpty()) {
			try {
				wait();
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}

		List<Request> elevatorQueue = elevQueueMap.get(e);

		List<Request> newRequests;
		if (elevatorQueue.isEmpty()) {
			newRequests = masterQueue.stream().filter(
					request -> request.getSourceFloor() == currentFloor
			).collect(Collectors.toList());

			if (newRequests.isEmpty()) {
				newRequests = Collections.singletonList(masterQueue.peek());
			}
		} else {
			newRequests = masterQueue.stream().filter(
					request -> request.getSourceFloor() == currentFloor && request.getDirection() == e.getDirection()
			).collect(Collectors.toList());
		}

		masterQueue.removeAll(newRequests);
		elevatorQueue.addAll(newRequests);
		return elevatorQueue;
	}

	/**
	 * Called by floor to end scheduler
	 */
	public void endActions() {
		doneReceiving = true;
	}

	public static void main(String[] args) {
		Scheduler scheduler = new Scheduler();
	}
}
