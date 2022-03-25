import static org.junit.jupiter.api.Assertions.*;

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
		BlockingDeque<DatagramPacket> reqsToServe = new LinkedBlockingDeque<>();
		Map<Integer, List<Request>> queueMap = Collections.synchronizedMap(new HashMap<>(Config.NUMBER_OF_ELEVATORS));
		Map<Integer, Integer> floorMap = Collections.synchronizedMap(new HashMap<>(Config.NUMBER_OF_ELEVATORS));
        s = new Scheduler(master, reqsToServe, queueMap, floorMap, false, false);
		e1 = new Elevator(1, 7, s.getPort());
		s.registerElevator(1);
		e2 = new Elevator(2, 7, s.getPort());
		s.registerElevator(2);
		e3 = new Elevator(3, 7, s.getPort());
		s.registerElevator(3);
		try {
			f = new Floor("test", s.getPort());
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	//Floor to Scheduler Communication
	
	/**
	 * Assert that the scheduler receives requests
	 */
	@Test
	public void testAddFloorRequestToScheduler() {
		f.requestElevator(3, 4, Direction.UP);
		s.getRequest();
		Request r = s.getMasterQueue().peek();
		assertEquals(r.getSourceFloor(), 3);
		assertEquals(r.getDestFloor(), 4);
		assertEquals(r.getDirection(), Direction.UP);
	}
	
	/**
     * Closes all the sockets so that the other Test classes can bind properly.
     */
    @AfterEach
    public void closeSockets() {
    	s.closeAllSockets();
    }
}
