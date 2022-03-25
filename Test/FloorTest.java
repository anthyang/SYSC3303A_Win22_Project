import static org.junit.jupiter.api.Assertions.*;

import java.net.*;
import java.util.*;
import java.util.concurrent.*;

import org.junit.jupiter.api.*;

/**
 * This is the JUnit test case for the floor subsystem in the elevator system
 */
class FloorTest {
    private static Floor f;
    private static Scheduler s;
    
    @BeforeAll
    public static void init() {
    	try {
    		BlockingDeque<Request> master = new LinkedBlockingDeque<>();
    		BlockingDeque<DatagramPacket> reqsToServe = new LinkedBlockingDeque<>();
    		Map<Integer, List<Request>> queueMap = Collections.synchronizedMap(new HashMap<>(Config.NUMBER_OF_ELEVATORS));
    		Map<Integer, Integer> floorMap = Collections.synchronizedMap(new HashMap<>(Config.NUMBER_OF_ELEVATORS));
            s = new Scheduler(master, reqsToServe, queueMap, floorMap, false, false);
			f = new Floor("test/inputTest", s.getPort());
		} catch (SocketException e) {
			e.printStackTrace();
		}
    }

    /**
     * This test is to test if the floor subsystem is able to read an input file or not
     */
    @Test
    public void TestFloor() throws InterruptedException {
        Thread floor = new Thread(f);
        floor.start();
        Thread.sleep(1000);
        assertTrue(f.doneReading());
    }
    
    /**
     * Asserts that a request for the specified floor is placed into the schedulers master queue.
     */
    @Test
    public void testRequestElevator() {
    	f.requestElevator(0, 4, Direction.UP, false);
    	s.getRequest();
    	Request r = s.getMasterQueue().poll();
    	assertEquals(r.getSourceFloor(), 0);
    	assertEquals(r.getDestFloor(), 4);
    	assertEquals(r.getDirection(), Direction.UP);
    }
    
    /**
     * Asserts that the requests in the input file are placed into the schedulers master queue.
     */
    @Test
    public void testReadInputFile() {
    	f.run();
    	if(f.doneReading()) {
    		s.getRequest();
        	Request r = s.getMasterQueue().poll();
        	assertEquals(r.getSourceFloor(), 2);
        	assertEquals(r.getDestFloor(), 5);
        	assertEquals(r.getDirection(), Direction.UP);
    		s.getRequest();
        	r = s.getMasterQueue().poll();
        	assertEquals(r.getSourceFloor(), 1);
        	assertEquals(r.getDestFloor(), 7);
        	assertEquals(r.getDirection(), Direction.UP);
    		s.getRequest();
        	r = s.getMasterQueue().poll();
        	assertEquals(r.getSourceFloor(), 4);
        	assertEquals(r.getDestFloor(), 1);
        	assertEquals(r.getDirection(), Direction.DOWN);
    	}
    }
    
    /**
     * Closes all the sockets so that the other Test classes can bind properly.
     */
    @AfterAll
    public static void closeSockets() {
    	s.closeAllSockets();
    }
}
