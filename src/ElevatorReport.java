import java.io.*;

/**
 * Message an elevator passes to the scheduler
 */
public class ElevatorReport implements Serializable {
    private int elevatorId;
    private Direction direction;
    private int arrivingAt;

    /**
     * Create an elevator report
     * @param elevatorId the id of the elevator issuing the report
     * @param direction the direction of the elevator
     * @param arrivingAt the elevator's current floor
     */
    public ElevatorReport(int elevatorId, Direction direction, int arrivingAt) {
        this.elevatorId = elevatorId;
        this.direction = direction;
        this.arrivingAt = arrivingAt;
    }

    /**
     * Get the elevator's ID
     * @return the elevator's ID
     */
    public int getElevatorId() {
        return elevatorId;
    }

    /**
     * Get the elevator's direction
     * @return the elevator's direction
     */
    public Direction getDirection() {
        return direction;
    }

    /**
     * Get the elevator's current floor
     * @return the elevator's current floor
     */
    public int getArrivingAt() {
        return arrivingAt;
    }
}
