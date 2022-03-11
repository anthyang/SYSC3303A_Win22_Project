import java.util.*;
import java.net.*;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * The scheduler class
 */
public class Scheduler extends Host implements Runnable{

	private BlockingDeque<Request> masterQueue;
	private BlockingDeque<DatagramPacket> elevatorsNeedingService;
	private Map<Integer, List<Request>> elevQueueMap;
	private Map<Integer, Integer> elevFloorMap;
	private boolean serveElevators;
	private boolean serveNewRequests;

	public static int ELEVATOR_UPDATE_PORT = 5000;
	public static int NEW_REQUEST_PORT = 5001;

	// add datagram
	private DatagramSocket socket;

	/**
	 * Scheduler constructor
	 */
	public Scheduler(
			BlockingDeque<Request> syncList,
			BlockingDeque<DatagramPacket> needingService,
			Map<Integer, List<Request>> queueMap,
			Map<Integer, Integer> floorMap,
			boolean serveElevators,
			boolean serveNewRequests
	) {
		super("Scheduler");
		this.masterQueue = syncList;
		elevQueueMap = queueMap;
		elevFloorMap = floorMap;
		this.elevatorsNeedingService = needingService;
		this.serveElevators = serveElevators;
		this.serveNewRequests = serveNewRequests;
		
	    try {
			if (serveElevators) {
				if (serveNewRequests) {
					socket = new DatagramSocket();
				} else {
					socket = new DatagramSocket(ELEVATOR_UPDATE_PORT);
				}
			} else {
				socket = new DatagramSocket(NEW_REQUEST_PORT);
			}
         } catch (SocketException se) {
            se.printStackTrace();
         }
	}

	/**
	 * Not thread safe. Use only for testing or single components
	 * @param serveElevators true to listen to elevators, false otherwise
	 */
	public Scheduler(boolean serveElevators) {
		super("Scheduler");
		this.masterQueue = new LinkedBlockingDeque<>();
		elevQueueMap = new HashMap<>(Config.NUMBER_OF_ELEVATORS);
		elevFloorMap = new HashMap<>(Config.NUMBER_OF_ELEVATORS);
		this.elevatorsNeedingService = new LinkedBlockingDeque<>();
		this.serveElevators = serveElevators;
		this.serveNewRequests = false;

		try {
			if (serveElevators) {
				socket = new DatagramSocket(ELEVATOR_UPDATE_PORT);
			} else {
				socket = new DatagramSocket(NEW_REQUEST_PORT);
			}
		} catch (SocketException se) {
			se.printStackTrace();
		}
	}

	/**
	 * Register an elevator with the scheduler
	 * @param id the elevator to register
	 */
	public void registerElevator(int id) {
		elevQueueMap.put(id, new ArrayList<>());
		elevFloorMap.put(id, 1); // Assume all elevators start at first floor
		this.log("Registering elevator " + id);
	}

	/**
	 * Update the elevator's queue
	 * @param e the elevator report
	 * @return if the elevator should stop
	 */
	public boolean updateQueue(ElevatorReport e) {
		int elevId = e.getElevatorId();
		List<Request> elevQueue = elevQueueMap.get(elevId);
		boolean elevatorShouldStop = false;

		this.elevFloorMap.put(elevId, e.getArrivingAt());

		Iterator<Request> queueIter = elevQueue.iterator();
		while (queueIter.hasNext()) {
			Request r = queueIter.next();
			// Check if elevator is serving request
			if (r.getSourceFloor() == e.getArrivingAt() || r.getDestFloor() == e.getArrivingAt()) {
				elevatorShouldStop = true;

				if (r.getDestFloor() == e.getArrivingAt()) {
					queueIter.remove();
				}
			}
		}

		if (!this.masterQueue.isEmpty() && elevQueue.stream().allMatch(request -> request.getDirection() == e.getDirection())) {
			// Check for additional requests, elevator is not travelling opposite direction to pickup
			synchronized (this.masterQueue) {
				Iterator<Request> masterIter = this.masterQueue.iterator();
				while (masterIter.hasNext()) {
					Request r = masterIter.next();
					if (r.getSourceFloor() == e.getArrivingAt() && r.getDirection() == e.getDirection()) {
						masterIter.remove();
						elevQueue.add(r);
						elevatorShouldStop = true;
					}
				}
			}
		}

		return elevatorShouldStop;
	}

	@Override
	public void run() {
		if (this.serveElevators) {
			if (this.serveNewRequests) {
				while (true) {
					this.dispatchRequest();
				}
			} else {
				this.log("Online and serving Elevators...");
				while (true) {
					this.handleRequests();
				}
			}
		} else {
			this.log("Online and awaiting for floor request...");
			while (true) {
				// Continuously listen to requests
				this.getRequest();
			}
		}
	}

	/**
	 * Accept new request from floor system.
	 */
	public void getRequest() {
		DatagramPacket response = this.receive(this.socket);

		byte[] data = response.getData();
		Request req = Host.deserialize(data, Request.class);  // convert byte to request
		this.log("Received Request from floor: source floor " + req.getSourceFloor());

		this.masterQueue.add(req);
	}

	/**
	 * assign request to an elevator
	 */
	public void dispatchRequest() {
		DatagramPacket elevReq;
		Request req;
		try {
			elevReq = this.elevatorsNeedingService.take();
			req = this.masterQueue.take();
		} catch (InterruptedException e) {
			throw new RuntimeException("Could not get elevator request");
		}

		int elevId = elevReq.getData()[0];
		int elevFloor = this.elevFloorMap.get(elevId);
		List<Request> elevQueue = this.elevQueueMap.get(elevId);
		elevQueue.add(req);

		byte[] r = new byte[1];
		if (elevFloor > req.getSourceFloor()) {
			r[0] = -1;
		} else if (elevFloor < req.getSourceFloor()) {
			r[0] = 1;
		} else {
			r[0] = 0;
		}

		this.send(this.socket, r, elevReq.getAddress(), elevReq.getPort());
	}

	/**
	 * route the request to the elevator subsystem.
	 */
	private void handleRequests() {
		DatagramPacket elevReq = this.receive(this.socket);
		if (elevReq.getLength() == 1) {
			int elevId = elevReq.getData()[0];
			// Elevator is stopped and is looking for a new request
			if (!this.elevQueueMap.containsKey(elevId)) {
				this.registerElevator(elevId);
			}

			List<Request> elevQueue = this.elevQueueMap.get(elevId);

			Request req;
			byte[] r = new byte[1];
			int elevFloor = this.elevFloorMap.get(elevId);
			if (elevQueue.isEmpty()) {
				this.elevatorsNeedingService.add(elevReq);
			} else {
				// serve request
				req = elevQueue.get(0);

				if (elevFloor > req.getDestFloor()) {
					r[0] = -1;
				} else if (elevFloor < req.getDestFloor()) {
					r[0] = 1;
				} else {
					r[0] = 0;
				}
				this.send(this.socket, r, elevReq.getAddress(), elevReq.getPort());
			}
		} else {
			// Elevator is sending a report
			ElevatorReport er = Host.deserialize(elevReq.getData(), ElevatorReport.class);
			this.log("Elevator " + er.getElevatorId() + " is at floor " + er.getArrivingAt());

			byte[] sendResp = {1};
			if (!this.updateQueue(er)) {
				sendResp[0] = 0;
			}
			this.send(this.socket, sendResp, elevReq.getAddress(), elevReq.getPort());
		}
	}

	public static void main(String[] args) {
		BlockingDeque<Request> master = new LinkedBlockingDeque<>();
		BlockingDeque<DatagramPacket> reqsToServe = new LinkedBlockingDeque<>();
		Map<Integer, List<Request>> queueMap = Collections.synchronizedMap(new HashMap<>(Config.NUMBER_OF_ELEVATORS));
		Map<Integer, Integer> floorMap = Collections.synchronizedMap(new HashMap<>(Config.NUMBER_OF_ELEVATORS));

		Scheduler elevScheduler = new Scheduler(master, reqsToServe, queueMap, floorMap, true, false);
		Scheduler reqScheduler = new Scheduler(master, reqsToServe, queueMap, floorMap, true, true);
		Scheduler floorScheduler = new Scheduler(master, reqsToServe, queueMap, floorMap, false, false);

		Thread elevSch = new Thread(elevScheduler);
		Thread reqSch = new Thread(reqScheduler);
		Thread floorSch = new Thread(floorScheduler);

		elevSch.start();
		reqSch.start();
		floorSch.start();
	}
	
	public Queue<Request> getMasterQueue(){
		return masterQueue;
	}
	
	public Map<Integer, List<Request>> getElevQueueMap(){
		return elevQueueMap;
	}

}
