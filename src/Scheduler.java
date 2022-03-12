import java.util.*;
import java.net.*;
import java.util.stream.Collectors;

/**
 * The scheduler class
 */
public class Scheduler extends Host implements Runnable{

	private Queue<Request> masterQueue;
	private Map<Integer, List<Request>> elevQueueMap;
	private boolean doneReceiving;
	private ElevatorReport report;
	private Elevator e;
	private int elevNum = Config.NUMBER_OF_ELEVATORS;

	public static int ELEVATOR_UPDATE_PORT = 5000;
	public static int NEW_REQUEST_PORT = 5001;
	
	// add datagram
	private DatagramSocket floorSocket, elevatorSocket;

	/**
	 * Scheduler constructor
	 */
	public Scheduler () {
		super("Scheduler");
		masterQueue = new LinkedList<>();
		elevQueueMap = new HashMap<>(elevNum);
		doneReceiving = false;
		
	    try {
        	elevatorSocket = new DatagramSocket();
        	floorSocket = new DatagramSocket(NEW_REQUEST_PORT);
         } catch (SocketException se) {
            se.printStackTrace();
         }
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
	public void registerElevator(int id) {
		elevQueueMap.put(id, new ArrayList<>());
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
	public synchronized List<Request> updateQueue(int id, int currentFloor) {
		if (doneReceiving && masterQueue.isEmpty() && elevQueueMap.get(id).isEmpty()) {
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
	@Override
	public void run() {
		this.log("Scheduler is online and awaiting for floor request...");
		while(true) {
			this.handleRequests(this.getRequest());			
		}		
	}
	/*
	 * Accept new request from floor system.
	 */
	public synchronized Request getRequest() {
		DatagramPacket response = this.receive(this.floorSocket);
		
		byte[] data = response.getData();
		Request req = super.deserialize(data);  // convert byte to request	
		return req;
	}
	/*
	 * route the request to the elevator subsystem.
	 * 
	 */
	private void handleRequests(Request req) {
		// assign request to an elevator:
		Direction dir = req.getDirection();
		byte[] r = new byte[0]; 
		switch(dir){
			case NOT_MOVING:
				r[0] = 0;
			case DOWN:
				r[0] = -1;
			default:
				r[0] = 1;			
		}	
		
		DatagramPacket response = this.rpcCall(this.elevatorSocket,r, InetAddress.getLoopbackAddress(), this.ELEVATOR_UPDATE_PORT );
		ElevatorReport resp = report.deserialize(response.getData());
		int currentFloor = resp.getArrivingAt();
		int e = resp.getElevatorId();	
		
		List<Request> elevList = updateQueue(id, currentFloor);
		byte[] sendResp = {1};
		if (elevList.isEmpty()) {
			sendResp[0] = 0;			
		}
		super.send(elevatorSocket, sendResp, InetAddress.getLoopbackAddress(), ELEVATOR_UPDATE_PORT);
	}	

	public static void main(String[] args) {
		Scheduler scheduler = new Scheduler();
		Thread sch = new Thread(scheduler);
		sch.start();
	}	
}
