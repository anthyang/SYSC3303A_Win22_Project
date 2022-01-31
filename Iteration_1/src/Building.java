/**
 * This is a general controller for the entire system
 */
public class Building {
    /** Number of elevators in the building */
    public static final int NUM_ELEVATORS = 1;
    /** Number of floors in the building */
    public static final int NUM_FLOORS = 7;

    public static void main(String[] args) {
        Scheduler scheduler = new Scheduler();

        Floor floor = new Floor(scheduler);
        Elevator elevator = new Elevator(1, scheduler, NUM_FLOORS);

        Thread floorSystem = new Thread(floor);
        floorSystem.start();

        Thread elevatorSystem = new Thread(elevator);
        elevatorSystem.start();
    }
}
