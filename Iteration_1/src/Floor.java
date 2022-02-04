import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * This class models a floor controller in the elevator system
 */
public class Floor implements Runnable {
    /** The input file for the program's requests */
    public String INPUT_FILE;

    /** Constants to reference different parts of the input file */
    private static final int TIME = 0;
    private static final int SOURCE_FLOOR = 1;
    private static final int DIRECTION_BUTTON = 2;
    private static final int DEST_FLOOR = 3;

    private Scheduler scheduler;

    /**
     * Create a new floor subsystem controller
     * @param scheduler The scheduler that will handle requests
     */
    public Floor(Scheduler scheduler, String inputName) {
        this.scheduler = scheduler;
        this.INPUT_FILE = "Iteration_1/src/" + inputName;
    }

    /**
     * Send an elevator request to the scheduler
     * @param sourceFloor Floor the new passenger is on
     * @param destFloor Passenger's destination floor
     * @param direction Direction pressed on the floor controller
     */
    public void requestElevator(int sourceFloor, int destFloor, Direction direction) {
        this.scheduler.addToServiceQueue(sourceFloor, destFloor, direction);
    }

    @Override
    public void run() {
        this.readInputFile();
    }

    /**
     * Parse the input file and queue requests for elevators
     */
    private void readInputFile() {
        try (BufferedReader br = new BufferedReader(new FileReader(INPUT_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Ignore comment lines
                if (!line.startsWith("#")) {
                    String[] instructions = line.split(" ");

                    Direction chosenDirection = Direction.get(instructions[DIRECTION_BUTTON]);
                    int sourceFloor = Integer.parseInt(instructions[SOURCE_FLOOR]);
                    int destFloor = Integer.parseInt(instructions[DEST_FLOOR]);

                    this.requestElevator(sourceFloor, destFloor, chosenDirection);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
