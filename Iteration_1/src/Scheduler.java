import java.util.List;

public class Scheduler {
    private List<List<Boolean>> queues;
    private boolean available;

    public Scheduler() {

    }

    public void addToServiceQueue(int floorNum, int elevDoorNum) {

    }

    public boolean getFloor(int elevDoorNum) {
        return true;
    }

    public void checkQueue(int floorNum, int elevDoorNum) {

    }

    public int maxQueueFloor() {
        return 7;
    }

    public int minQueueFloor() {
        return 1;
    }

    public void removeFromQueue() {

    }

    public Request getAvailRequest() {
        return null;
    }

    public List<Request> checkRequest(int currentFloor, Direction direction) {
        return null;
    }
}
