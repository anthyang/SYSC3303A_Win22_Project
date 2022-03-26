import java.io.Serializable;

/**
 * Represents a request for an elevator
 */
public class Request implements Serializable {
    private int sourceFloor;
    private int destFloor;
    private Direction direction;
    private boolean triggerFault;
    private boolean pickedUp;

    /**
     * The request constructor
     * @param sourceFloor The floor the passenger is on
     * @param destFloor The floor the passenger would like to visit
     * @param direction The direction the elevator needs to move
     */
    public Request(int sourceFloor, int destFloor, Direction direction, boolean triggerFault) {
        this.sourceFloor = sourceFloor;
        this.destFloor = destFloor;
        this.direction = direction;
        this.triggerFault = triggerFault;
        this.pickedUp = false;
    }

    /**
     * Get the request's destination floor
     * @return the request's destination floor
     */
    public int getDestFloor() {
        return destFloor;
    }

    /**
     * Set the request's source floor
     * @param i the request's new source floor
     */
    public void setSourceFloor(int i) {
        this.sourceFloor = i;
    }

    /**
     * Get the request's source floor
     * @return the request's source floor
     */
    public int getSourceFloor() {
        return sourceFloor;
    }

    /**
     * Get the request's direction
     * @return the request's direction
     */
    public Direction getDirection() {
        return direction;
    }

    /**
     * Check whether the request should trigger a fault in the elevator
     * @return true if a fault will be triggered
     */
    public boolean isTriggerFault() {
        return triggerFault;
    }

    /**
     * Mark the request as picked up by the elevator
     */
    public void pickUp() {
        this.pickedUp = true;
    }

    /**
     * Drop off passengers in case of a fault
     */
    public void dropOff() {
        this.pickedUp = false;
    }

    /**
     * Check whether the request has been picked up
     * @return true if the request has been picked up
     */
    public boolean isPickedUp() {
        return pickedUp;
    }
}
