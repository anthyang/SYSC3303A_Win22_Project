import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.InetAddress;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

//TODO make tests to floorLamps, currentFloor, direction, doorsOpen, serveNewRequest(), simMovement(), openDoor(), closeDoor(), addExternalRequest(), registerElevator()


/**
 * This is the JUnit test case for the Elevator subsystem in the Elevator system
 */
class ElevatorTest {
    private static Scheduler s;
    private static Elevator e;

    @BeforeEach
    public void init() {
    	BlockingDeque<Request> master = new LinkedBlockingDeque<>();
		BlockingDeque<Integer> reqsToServe = new LinkedBlockingDeque<>();
		Map<Integer, ElevatorStatus> elevators = Collections.synchronizedMap(new HashMap<>(Config.NUMBER_OF_ELEVATORS));
        s = new Scheduler(master, reqsToServe, elevators, true, false, new ConsoleGUI());
    	e = new Elevator(0, 10, s.getPort());
    }

    /**
     * This test is to test for the simulation of movement using the simMovement() method in the elevator class
     */
    @Test
    public void testSimMov() {
    	e.setElevatorDirection(Direction.UP);
        e.simMovement();
        assertEquals(2, e.getCurrentFloor());
        e.simMovement();
        assertEquals(3, e.getCurrentFloor());
    	e.setElevatorDirection(Direction.DOWN);
        e.simMovement();
        assertEquals(2, e.getCurrentFloor());
    }
    
    /**
     * Asserts that the elevator moves to serve a request
     */
    @Test
    public void testServeNewRequest() {
    	e.setElevatorDirection(Direction.UP);
    	s.registerElevator(1, InetAddress.getLoopbackAddress(), 9999);
    	for(int i = 0; i < 2; i++) {
    		e.simMovement();
    	}
    	assertEquals(3, e.getCurrentFloor());
    }    
    /**
     * test transient/hard error in elevator
     */
    @Test
    public void testError() {
    	ElevatorStatus e = new ElevatorStatus(0, 1, Direction.UP, InetAddress.getLoopbackAddress(), 9999);
    	e.setInactive();
    	assertEquals(e.isActive(), false);
    }  
    
    
    /**
     * Closes all the sockets so that the other Test classes can bind properly.
     */
    @AfterEach
    public void closeSockets() {
    	s.closeSockets();
    }
}
