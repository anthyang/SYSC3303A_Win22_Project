import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        e = new Elevator(1, Config.NUMBER_OF_FLOORS);
        
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
     * test for transient error 
     */
    @Test
    public void testTransientError() {
        Request r = new Request(1, 4, Direction.UP, "transient");         
        s.registerElevator(1, InetAddress.getLoopbackAddress(), 9999);     
        ElevatorReport eReport = new ElevatorReport(1, Direction.UP, 4);
        ElevatorStatus elevator = s.getElevMap().get(eReport.getElevatorId());
        elevator.addRequestToService(r);
     	assertEquals(s.updateQueue(eReport), 2); 
    }    
    
    /**
     * test for hard error 
     */
    @Test
    public void testHardError() {
        Request r = new Request(1, 4, Direction.UP, "hard");
        s.registerElevator(1, InetAddress.getLoopbackAddress(), 9999);     
        ElevatorReport eReport = new ElevatorReport(1, Direction.UP, 4);
        ElevatorStatus elevator = s.getElevMap().get(eReport.getElevatorId());
        elevator.setInactive();
        s.shutDownElevator(1);
        elevator.addRequestToService(r);
        assertEquals(s.updateQueue(eReport), 3);
    } 
         
    /**
     * Closes all the sockets so that the other Test classes can bind properly.
     */
    @AfterEach
    public void closeSockets() {
    	s.closeSockets();
    }
    
}

