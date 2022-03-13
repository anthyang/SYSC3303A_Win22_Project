import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Set;

//TODO make tests to floorLamps, currentFloor, direction, doorsOpen, serveNewRequest(), simMovement(), openDoor(), closeDoor(), addExternalRequest(), registerElevator()


/**
 * This is the JUnit test case for the Elevator subsystem in the Elevator system
 */
class ElevatorTest {
    private static Scheduler s;
    private static Elevator e;

    @BeforeEach
    public void init() {
        s = new Scheduler(true);
    	e = new Elevator(0, 10);

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
    
}
