import java.net.InetAddress;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Internal data structure for the scheduler to keep track of all elevator data
 */
public class ElevatorStatus {
    private int id;
    private Direction direction;
    private int currentFloor;
    private boolean active;
    private long lastReport;
    private InetAddress address;
    private int port;
    private List<Request> serviceQueue;

    /**
     * ElevatorStatus constructor
     * @param startingFloor the elevator's starting floor
     * @param direction the elevator's starting direction
     * @param address the elevator's IP address
     * @param port the elevator's port
     */
    public ElevatorStatus(int id, int startingFloor, Direction direction, InetAddress address, int port) {
        this.id = id;
        this.currentFloor = startingFloor;
        this.direction = direction;
        this.active = true;
        this.address = address;
        this.port = port;
        this.serviceQueue = Collections.synchronizedList(new LinkedList<>());
        this.lastReport = System.currentTimeMillis();
    }

    /**
     * Get the elevator's id
     * @return the elevator's id
     */
    public int getId() {
        return id;
    }

    /**
     * Get the elevator's direction
     * @return the elevator's direction
     */
    public Direction getDirection() {
        return direction;
    }

    /**
     * Change the elevator's direction
     * @param direction the elevator's new direction
     */
    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    /**
     * Get the elevator's current floor
     * @return the elevator's current floor
     */
    public int getCurrentFloor() {
        return currentFloor;
    }

    /**
     * Update the elevator's current floor
     * @param currentFloor the elevator's new floor
     */
    public void setCurrentFloor(int currentFloor) {
        this.currentFloor = currentFloor;
    }

    /**
     * Get the elevator's IP address
     * @return the elevator's address
     */
    public InetAddress getAddress() {
        return address;
    }

    /**
     * Get the elevator's port
     * @return the elevator's port
     */
    public int getPort() {
        return port;
    }

    /**
     * Get the elevator's service queue
     * @return the elevator's service queue
     */
    public List<Request> getServiceQueue() {
        return serviceQueue;
    }

    /**
     * Add a request to the elevator's service queue
     * @param r the request to add to the service queue
     */
    public void addRequestToService(Request r) {
        this.serviceQueue.add(r);
    }

    /**
     * Check whether the elevator should stop at the current floor
     * @return if the elevator should stop at the current floor
     */
    public boolean shouldStopAtCurrentFloor() {
        return this.serviceQueue.stream().anyMatch(
                request -> request.getSourceFloor() == this.currentFloor || request.getDestFloor() == this.currentFloor
        );
    }

    /**
     * Remove requests from service queue if the destination is the current floor
     */
    public void serviceFloor() {
        this.serviceQueue.removeIf(request -> request.getDestFloor() == this.currentFloor);

        for (Request r : serviceQueue) {
            if (r.getSourceFloor() == this.currentFloor) {
                r.pickUp();
            }
        }
    }

    /**
     * Check whether the elevator is still active
     * @return true if the elevator is active
     */
    public boolean isActive() {
        return this.active;
    }

    /**
     * Set a fault detected on the elevator
     */
    public void setInactive() {
        this.active = false;
    }

    /**
     * Get a timestamp of the elevator's last communication with the scheduler
     * @return the time of the last communication with the scheduler
     */
    public long getLastReport() {
        return this.lastReport;
    }

    /**
     * Update the time of last communication
     * @param time the current time
     */
    public void refreshLastReport(long time) {
        this.lastReport = time;
    }
}
