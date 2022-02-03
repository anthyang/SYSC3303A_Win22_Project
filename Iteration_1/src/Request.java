public class Request {
    private int sourceFloor;
    private int destFloor;
    private Direction direction;

    public Request(int sourceFloor, int destFloor, Direction direction) {
        this.sourceFloor = sourceFloor;
        this.destFloor = destFloor;
        this.direction = direction;
    }

    public int getDestFloor() {
        return destFloor;
    }

    public int getSourceFloor() {
        return sourceFloor;
    }

    public Direction getDirection() {
        return direction;
    }
}
