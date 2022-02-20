/**
 * Represents a request for an elevator
 */
public class Request {
    private int sourceFloor;
    private int destFloor;
    private Direction direction;

    /**
     * The request constructor
     * @param sourceFloor The floor the passenger is on
     * @param destFloor The floor the passenger would like to visit
     * @param direction The direction the elevator needs to move
     */
    public Request(int sourceFloor, int destFloor, Direction direction) {
        this.sourceFloor = sourceFloor;
        this.destFloor = destFloor;
        this.direction = direction;
    }

    /**
     * Get the request's destination floor
     * @return the request's destination floor
     */
    public int getDestFloor() {
        return destFloor;
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
}
