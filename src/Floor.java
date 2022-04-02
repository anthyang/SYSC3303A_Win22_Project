import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.*;

/**
 * This class models a floor controller in the elevator system
 */
public class Floor extends Host implements Runnable {
    /** The input file for the program's requests */
    private String inputFile;
    private boolean finished_reading = false;
    private int sendPort;

    /**
     * Create a new floor subsystem controller
     * @param inputName is the name of the input file
     */
    public Floor(String inputName) throws SocketException {
        super("Floor");
        this.inputFile = inputName;
        this.sendPort = Scheduler.NEW_REQUEST_PORT;
    }

    /**
     * Create a new floor subsystem controller. Override the default send port
     * @param inputName is the name of the input file
     */
    public Floor(String inputName, int sendPort) throws SocketException {
        super("Floor");
        this.inputFile = inputName;
        this.sendPort = sendPort;
    }

    /**
     * Send an elevator request to the scheduler
     * @param sourceFloor Floor the new passenger is on
     * @param destFloor Passenger's destination floor
     * @param direction Direction pressed on the floor controller
     */
    public void requestElevator(int sourceFloor, int destFloor, Direction direction, String triggerFault) {
    	Request r = new Request(sourceFloor, destFloor, direction, triggerFault);
        byte[] s_request = Host.serialize(r);   //Turn the request object into a byte array
        super.send(s_request, InetAddress.getLoopbackAddress(), this.sendPort);    //send the request
        super.log("send request to scheduler.");
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
                    String triggerFault = new String(instructions[Config.TRIGGER_FAULT]);

                    this.requestElevator(sourceFloor, destFloor, chosenDirection, triggerFault);
                }
            }
            finished_reading = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Check if the subsystem has finished reading the input
     * @return true if the system is done, false otherwise
     */
    public boolean doneReading() {return finished_reading;}

    public static void main(String[] args) throws SocketException {
        Floor floor = new Floor("src/input");
        Thread floorSystem = new Thread(floor);
        floorSystem.start();
    }
}
