import org.junit.jupiter.api.*;

import static org.junit.Assert.assertTrue;

import java.net.*;
import java.util.*;
import java.util.concurrent.*;


/**
 * This is the JUnit test case for the Scheduler in the Elevator system
 */
class SchedulerTest {
    private static Elevator e;
    private static Scheduler s;

    @BeforeEach
    public void init() {
    	BlockingDeque<Request> master = new LinkedBlockingDeque<>();
		BlockingDeque<DatagramPacket> reqsToServe = new LinkedBlockingDeque<>();
		Map<Integer, List<Request>> queueMap = Collections.synchronizedMap(new HashMap<>(Config.NUMBER_OF_ELEVATORS));
		Map<Integer, Integer> floorMap = Collections.synchronizedMap(new HashMap<>(Config.NUMBER_OF_ELEVATORS));
        s = new Scheduler(master, reqsToServe, queueMap, floorMap, true, false);
        e = new Elevator(1, Config.NUMBER_OF_FLOORS, s.getPort());
        
    }

    /**
     * Asserts that elevators are added with registerElevator()
     */
    @Test
    public void testRegisterElevator() {
    	s.registerElevator(1);
    	assertTrue(s.getElevQueueMap().containsKey(1));
    }
    
    /**
     * Closes all the sockets so that the other Test classes can bind properly.
     */
    @AfterEach
    public void closeSockets() {
    	s.closeAllSockets();
    }
    
}

