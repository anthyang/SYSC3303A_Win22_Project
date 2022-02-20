import java.util.*;
import java.util.stream.Collectors;

public class Scheduler {

	private Queue<Request> masterQueue;
	private Map<Elevator, List<Request>> elevQueueMap;
	private boolean doneReceiving;

	public Scheduler() {
		masterQueue = new LinkedList<>();
		elevQueueMap = new HashMap<>(Config.NUMBER_OF_ELEVATORS);
		doneReceiving = false;
	}

	public void addExternalRequest(Request request) {
		masterQueue.add(request);
	}

	public void registerElevator(Elevator e) {
		elevQueueMap.put(e, new ArrayList<>());
	}

	public void serviceComplete(Elevator e, int destFloor) {
		elevQueueMap.get(e).removeIf(request -> request.getDestFloor() == destFloor);
	}

	public List<Request> updateQueue(Elevator e, int currentFloor) {
		if (doneReceiving && masterQueue.isEmpty() && elevQueueMap.get(e).isEmpty()) {
			return null;
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
	
	//called by floor to end scheduler
	public void endActions() {
		doneReceiving = true;
	}
}
