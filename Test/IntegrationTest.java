import static org.junit.jupiter.api.Assertions.*;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Test;

//TODO make tests for open door after serving request, close door while serving request, elevator moves to the proper floor, ensure elevator requests are served by the proper elevator, floor is removed from queue upon service, updateQueue(), getRequest(), handleRequest(), readInputFile()

public class IntegrationTest {
	
	private static Floor f;
	private static Scheduler s;
	private static Elevator e1;
	private static Elevator e2;
	private static Elevator e3;

	/**
	 * Initialization of integration testing environment;
	 */
	@BeforeEach
	public void init() {
		BlockingDeque<Request> master = new LinkedBlockingDeque<>();
		BlockingDeque<Integer> reqsToServe = new LinkedBlockingDeque<>();
		Map<Integer, ElevatorStatus> elevators = Collections.synchronizedMap(new HashMap<>(Config.NUMBER_OF_ELEVATORS));
        s = new Scheduler(master, reqsToServe, elevators, false, false, new ConsoleGUI());
		e1 = new Elevator(1, 7);
		s.registerElevator(1, InetAddress.getLoopbackAddress(), 9999);
		e2 = new Elevator(2, 7);
		s.registerElevator(2, InetAddress.getLoopbackAddress(), 9999);
		e3 = new Elevator(3, 7);
		s.registerElevator(3, InetAddress.getLoopbackAddress(), 9999);
		f = new Floor("test");
	}
	
	//Floor to Scheduler Communication
	
	/**
	 * Assert that the scheduler receives requests
	 */
	@Test
	public void testAddFloorRequestToScheduler() {
		f.requestElevator(3, 4, Direction.UP, "none");
		s.getRequest();
		Request r = s.getMasterQueue().peek();
		assertEquals(r.getSourceFloor(), 3);
		assertEquals(r.getDestFloor(), 4);
		assertEquals(r.getDirection(), Direction.UP);
	}
	
    /**
     * Test transient faults for UP and DOWN directions
     */
    @Test
    public void testTransientFault() {
    	// test for transient fault:
		Request r = new Request(1, 4, Direction.UP, "transient");
    	assertEquals(r.isTriggerFault(), "transient");   
    	// test for transient fault:
		Request r2 = new Request(6, 2, Direction.DOWN, "transient");
    	assertEquals(r.isTriggerFault(), "transient"); 
    }    
    
    /**
     * Test for hard faults for UP and DOWN directions
     */
    @Test
    public void testHardFault() {
    	// test for transient fault:
		Request r3 = new Request(1, 4, Direction.UP, "hard");
    	assertEquals(r3.isTriggerFault(), "hard");   
    	// test for transient fault:
		Request r4 = new Request(6, 2, Direction.DOWN, "hard");
    	assertEquals(r4.isTriggerFault(), "hard"); 
    }    
	
	
	/**
     * Closes all the sockets so that the other Test classes can bind properly.
     */
    @AfterEach
    public void closeSockets() {
    	s.closeSockets();
    }
}
