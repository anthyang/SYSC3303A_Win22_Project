import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * This is the JUnit test case for the Scheduler in the Elevator system
 */
class SchedulerTest {
    private static Elevator e;
    private static Scheduler s;

    @BeforeEach
    public void init() {
    	BlockingDeque<Request> master = new LinkedBlockingDeque<>();
		BlockingDeque<Integer> reqsToServe = new LinkedBlockingDeque<>();
		Map<Integer, ElevatorStatus> elevators = Collections.synchronizedMap(new HashMap<>(Config.NUMBER_OF_ELEVATORS));
        s = new Scheduler(master, reqsToServe, elevators, true, false, new ConsoleGUI());
        e = new Elevator(1, Config.NUMBER_OF_FLOORS, s.getPort());
        
    }

    /**
     * Asserts that elevators are added with registerElevator()
     */
    @Test
    public void testRegisterElevator() {
    	s.registerElevator(1, InetAddress.getLoopbackAddress(), 9999);
    	assertTrue(s.getElevMap().containsKey(1));
    }
    
    /**
     * Closes all the sockets so that the other Test classes can bind properly.
     */
    @AfterEach
    public void closeSockets() {
    	s.closeSockets();
    }
    
}

