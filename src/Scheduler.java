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
	private ArrayList<String> faultList = new ArrayList<String>();

	public static int ELEVATOR_UPDATE_PORT = 5000;
	public static int NEW_REQUEST_PORT = 5001;

	public ConsoleGUI gui;

	/**
	 * Scheduler constructor
	 */
	public Scheduler(
			BlockingDeque<Request> syncList,
			BlockingDeque<Integer> needingService,
			Map<Integer, ElevatorStatus> elevators,
			boolean serveElevators,
			boolean serveNewRequests,
			ConsoleGUI ui
	) {
		super("Scheduler", serveElevators ? (serveNewRequests ? 0 : ELEVATOR_UPDATE_PORT) : NEW_REQUEST_PORT);
		this.masterQueue = syncList;
		this.elevatorsNeedingService = needingService;
		this.elevators = elevators;
		this.serveElevators = serveElevators;
		this.serveNewRequests = serveNewRequests;
		this.gui = ui;
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
		String[] faultTypes = {"hard", "transient"};

		boolean elevatorShouldStop = elevator.shouldStopAtCurrentFloor();
		boolean triggerFault = elevator.getServiceQueue().stream().anyMatch(
				request -> Arrays.stream(faultTypes).anyMatch(request.isTriggerFault()::contains) &&
						request.getDestFloor() == elevator.getCurrentFloor()
		);
		String trigger = "";
		if(triggerFault){
			trigger = elevator.getServiceQueue().stream().filter(request -> Arrays.stream(faultTypes).anyMatch(request.isTriggerFault()::contains)
					&& request.getDestFloor() == elevator.getCurrentFloor()).findFirst().get().isTriggerFault();
			String fault = "Fault find in elevator " + elevId + ": " + trigger;
			log(fault);
			faultList.add(fault);
			compileFaults();
		}

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
			if(trigger.equals("hard")){
				return 3;
			}else if(trigger.equals("transient")){
				return 2;
			}else{
				return 1;
			}
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
			byte[] sendResp = { (byte)this.updateQueue(er) };
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
		String fault = "Elevator " + elevId + " has timed out. Assuming fault.";
		this.log(fault);
		faultList.add(fault);
		compileFaults();

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

	public void compileFaults() {
		StringBuilder sb = new StringBuilder();
		for(String s : faultList) {
			sb.append(s).append("\n").append("\n");
		}
		gui.displayFaults(sb.toString());
	}
}