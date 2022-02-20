/**
 * This is a general controller for the entire system
 */
public class Building {
    public static void main(String[] args) {
        Scheduler scheduler = new Scheduler();

        Floor floor = new Floor(scheduler,"src/input");
        Elevator elevator = new Elevator(1, scheduler, Config.NUMBER_OF_FLOORS);

        Thread floorSystem = new Thread(floor);
        floorSystem.start();

        Thread elevatorSystem = new Thread(elevator);
        elevatorSystem.start();
    }
}
