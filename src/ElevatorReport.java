import java.io.*;

public class ElevatorReport implements Serializable {
    private int elevatorId;
    private Direction direction;
    private int arrivingAt;

    public ElevatorReport(int elevatorId, Direction direction, int arrivingAt) {
        this.elevatorId = elevatorId;
        this.direction = direction;
        this.arrivingAt = arrivingAt;
    }

    public int getElevatorId() {
        return elevatorId;
    }

    public Direction getDirection() {
        return direction;
    }

    public int getArrivingAt() {
        return arrivingAt;
    }
}
