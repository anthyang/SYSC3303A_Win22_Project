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

    public byte[] serialize() {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();

        try {
            ObjectOutputStream oos = new ObjectOutputStream(byteOut);
            oos.writeObject(this);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return byteOut.toByteArray();
    }

    public static ElevatorReport deserialize(byte[] serializedData) {
        ElevatorReport report;
        ByteArrayInputStream byteIn = new ByteArrayInputStream(serializedData);

        try {
            ObjectInputStream ois = new ObjectInputStream(byteIn);
            report = (ElevatorReport) ois.readObject();
            ois.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        return report;
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
