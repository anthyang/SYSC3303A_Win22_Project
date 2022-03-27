import java.util.*;
import java.net.*;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * The scheduler class
 */
public class Scheduler extends Host implements Runnable{

	private BlockingDeque<Request> masterQueue;
	private BlockingDeque<Integer> elevatorsNeedingService;
	private Map<Integer, ElevatorStatus> elevators;
	private static Timer timer = new Timer();
	private boolean serveElevators;
	private boolean serveNewRequests;

	public static int ELEVATOR_UPDATE_PORT = 5000;
	public static int NEW_REQUEST_PORT = 5001;

	/**
	 * Scheduler constructor
	 */
	public Scheduler(
			BlockingDeque<Request> syncList,
			BlockingDeque<Integer> needingService,
			Map<Integer, ElevatorStatus> elevators,
			boolean serveElevators,
			boolean serveNewRequests
	) {
		super("Scheduler", serveElevators ? (serveNewRequests ? 0 : ELEVATOR_UPDATE_PORT) : NEW_REQUEST_PORT);
		this.masterQueue = syncList;
		this.elevatorsNeedingService = needingService;
		this.elevators = elevators;
		this.serveElevators = serveElevators;
		this.serveNewRequests = serveNewRequests;
	}

	/**
	 * Register an elevator with the scheduler
	 * @param id the elevator to register
	 */
	public void registerElevator(int id, InetAddress address, int port) {
		// Assume all elevators start at first floor
		this.elevators.put(id, new ElevatorStatus(id,1, Direction.NOT_MOVING, address, port));
		this.log("Registering elevator " + id);
	}

	/**
	 * Update the elevator's queue
	 * @param e the elevator report
	 * @return > 1 if the elevator should stop, 0 if the elevator should continue
	 */
	public int updateQueue(ElevatorReport e) {
		int elevId = e.getElevatorId();
		ElevatorStatus elevator = elevators.get(elevId);
		elevator.setCurrentFloor(e.getArrivingAt());
		elevator.setDirection(e.getDirection());

		boolean elevatorShouldStop = elevator.shouldStopAtCurrentFloor();
		boolean triggerFault = elevator.getServiceQueue().stream().anyMatch(
				request -> request.isTriggerFault() && request.getDestFloor() == elevator.getCurrentFloor()
		);

		if (elevatorShouldStop) {
			elevator.serviceFloor();
		}

		if (!this.masterQueue.isEmpty() && (elevator.getServiceQueue().stream().allMatch(Request::isPickedUp))
		) {
			// Check for additional requests, elevator is in transit
			synchronized (this.masterQueue) {
				Iterator<Request> masterIter = this.masterQueue.iterator();
				while (masterIter.hasNext()) {
					Request r = masterIter.next();
					if (r.getSourceFloor() == elevator.getCurrentFloor() && r.getDirection() == elevator.getDirection()) {
						masterIter.remove();
						this.log("Passing request with source floor " + r.getSourceFloor() + " to elevator " + elevId);
						elevator.addRequestToService(r);
						elevatorShouldStop = true;
					}
				}
			}
		}

		if (elevatorShouldStop) {
			return triggerFault ? 2 : 1;
		} else {
			return 0;
		}
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
		DatagramPacket response = this.receive();

		byte[] data = response.getData();
		Request req = Host.deserialize(data, Request.class);  // convert byte to request
		this.log("Received Request from floor: source floor " + req.getSourceFloor());

		this.masterQueue.add(req);
	}

	/**
	 * assign request to an elevator
	 */
	public void dispatchRequest() {
		ElevatorStatus elevator;
		Request req;
		try {
			do {
				// Get next request for an elevator that has no fault detected
				elevator = this.elevators.get(this.elevatorsNeedingService.take());
			} while (!elevator.isActive());

			// Schedule next request in master queue
			req = this.masterQueue.take();
		} catch (InterruptedException e) {
			throw new RuntimeException("Could not get elevator request");
		}

		this.log("Passing request with source floor " + req.getSourceFloor() + " to elevator " + elevator.getId());
		elevator.addRequestToService(req);

		int elevFloor = elevator.getCurrentFloor();

		byte[] r = new byte[1];
		if (elevFloor > req.getSourceFloor()) {
			r[0] = -1;
		} else if (elevFloor < req.getSourceFloor()) {
			r[0] = 1;
		} else {
			r[0] = 0;
		}

		this.send(r, elevator.getAddress(), elevator.getPort());
	}

	/**
	 * route the request to the elevator subsystem.
	 */
	private void handleRequests() {
		DatagramPacket elevReq = this.receive();
		if (elevReq.getLength() == 1) {
			// Elevator is stopped and is looking for a new request
			int elevId = elevReq.getData()[0];

			if (!this.elevators.containsKey(elevId)) {
				this.registerElevator(elevId, elevReq.getAddress(), elevReq.getPort());
			}

			ElevatorStatus elevator = this.elevators.get(elevId);
			elevator.refreshLastReport(System.currentTimeMillis());

			Request req;
			byte[] r = new byte[1];
			int elevFloor = elevator.getCurrentFloor();
			if (elevator.getServiceQueue().isEmpty()) {
				this.elevatorsNeedingService.add(elevId);
			} else {
				// serve request
				req = elevator.getServiceQueue().get(0);

				if (elevFloor > req.getDestFloor()) {
					r[0] = -1;
				} else if (elevFloor < req.getDestFloor()) {
					r[0] = 1;
				} else {
					r[0] = 0;
				}
				this.send(r, elevator.getAddress(), elevator.getPort());
			}
		} else {
			// Elevator is sending a report
			ElevatorReport er = Host.deserialize(elevReq.getData(), ElevatorReport.class);
			int elevId = er.getElevatorId();
			this.log("Elevator " + elevId + " is at floor " + er.getArrivingAt());

			ElevatorStatus elevator = this.elevators.get(elevId);
			elevator.setCurrentFloor(er.getArrivingAt());
			elevator.setDirection(er.getDirection());

			long arrivedTime = System.currentTimeMillis();
			elevator.refreshLastReport(arrivedTime);

			// Schedule check to ensure new report before timeout
			checkTimeout(elevator, elevId, arrivedTime);

			byte[] sendResp = {0};
			int updateResult = this.updateQueue(er);
			if (updateResult > 0) {
				if (updateResult == 2) {
					// Trigger a fault
					sendResp[0] = 2;
				} else {
					sendResp[0] = 1;
				}
			}
			this.send(sendResp, elevator.getAddress(), elevator.getPort());
		}
	}

	public void checkTimeout(ElevatorStatus e, int elevId, long time) {
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				if (e.getLastReport() == time) {
					shutDownElevator(elevId);
				}
			}
		}, Config.TIMEOUT);
	}

	/**
	 * Shut down an elevator
	 * @param elevId the id of the elevator to shut down
	 */
	public void shutDownElevator(int elevId) {
		this.log("Elevator " + elevId + " has timed out. Assuming fault.");

		ElevatorStatus elevator = this.elevators.get(elevId);
		elevator.setInactive();

		// Transfer elevator's requests back to the master queue
		for (Request r : elevator.getServiceQueue()) {
			if (r.isPickedUp()) {
				r.setSourceFloor(elevator.getCurrentFloor());
				r.dropOff();
			}

			this.masterQueue.addFirst(r);
		}

		elevator.getServiceQueue().clear();
	}
	
	/**
	 * Calls Host class to close the socket.
	 */
	public void closeSockets() {
		this.closeSocket();
	}

	public static void main(String[] args) {
		BlockingDeque<Request> master = new LinkedBlockingDeque<>();
		BlockingDeque<Integer> reqsToServe = new LinkedBlockingDeque<>();
		Map<Integer, ElevatorStatus> elevators = Collections.synchronizedMap(new HashMap<>(Config.NUMBER_OF_ELEVATORS));

		Scheduler elevScheduler = new Scheduler(master, reqsToServe, elevators, true, false);
		Scheduler reqScheduler = new Scheduler(master, reqsToServe, elevators, true, true);
		Scheduler floorScheduler = new Scheduler(master, reqsToServe, elevators, false, false);

		Thread elevSch = new Thread(elevScheduler);
		Thread reqSch = new Thread(reqScheduler);
		Thread floorSch = new Thread(floorScheduler);

		elevSch.start();
		reqSch.start();
		floorSch.start();
	}

	/**
	 * Get the scheduler's master queue
	 * @return the scheduler's master queue
	 */
	public Queue<Request> getMasterQueue(){
		return masterQueue;
	}

	/**
	 * Get the scheduler's elevator map
	 * @return the scheduler's elevator map
	 */
	public Map<Integer, ElevatorStatus> getElevMap(){
		return elevators;
	}
}
