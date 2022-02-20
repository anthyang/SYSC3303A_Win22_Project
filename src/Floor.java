import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * This class models a floor controller in the elevator system
 */
public class Floor implements Runnable {
    /** The input file for the program's requests */
    public String inputFile;
    private boolean finished_reading = false;

    private Scheduler scheduler;

    /**
     * Create a new floor subsystem controller
     * @param scheduler The scheduler that will handle requests
     */
    public Floor(Scheduler scheduler, String inputName) {
        this.scheduler = scheduler;
        this.inputFile = inputName;
    }

    /**
     * Send an elevator request to the scheduler
     * @param sourceFloor Floor the new passenger is on
     * @param destFloor Passenger's destination floor
     * @param direction Direction pressed on the floor controller
     */
    public void requestElevator(int sourceFloor, int destFloor, Direction direction) {
        //this.scheduler.addToServiceQueue(sourceFloor, destFloor, direction);
    	Request r = new Request(sourceFloor, destFloor, direction);
        this.scheduler.addExternalRequest(r);
    }

    @Override
    public void run() {
        this.readInputFile();
    }

    /**
     * Parse the input file and queue requests for elevators
     */
    private void readInputFile() {
        try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Ignore comment lines
                if (!line.startsWith("#")) {
                    String[] instructions = line.split(" ");

                    Direction chosenDirection = Direction.get(instructions[Config.DIRECTION_BUTTON]);
                    int sourceFloor = Integer.parseInt(instructions[Config.SOURCE_FLOOR]);
                    int destFloor = Integer.parseInt(instructions[Config.DEST_FLOOR]);

                    this.requestElevator(sourceFloor, destFloor, chosenDirection);
                }
            }
            finished_reading = true;
            scheduler.endActions();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean doneReading() {return finished_reading;}
}
