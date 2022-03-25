import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
		BlockingDeque<DatagramPacket> reqsToServe = new LinkedBlockingDeque<>();
		Map<Integer, List<Request>> queueMap = Collections.synchronizedMap(new HashMap<>(Config.NUMBER_OF_ELEVATORS));
		Map<Integer, Integer> floorMap = Collections.synchronizedMap(new HashMap<>(Config.NUMBER_OF_ELEVATORS));
        s = new Scheduler(master, reqsToServe, queueMap, floorMap, true, false);
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
    	s.registerElevator(1);
    	s.getElevQueueMap().get(1).add(new Request(1, 3, Direction.UP));
    	for(int i = 0; i < 2; i++) {
    		e.simMovement();
    	}
    	assertEquals(3, e.getCurrentFloor());
    }
    
    /**
     * Closes all the sockets so that the other Test classes can bind properly.
     */
    @AfterEach
    public void closeSockets() {
    	s.closeAllSockets();
    }
}
