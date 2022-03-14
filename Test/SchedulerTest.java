import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Queue;


/**
 * This is the JUnit test case for the Scheduler in the Elevator system
 */
class SchedulerTest {
    private static Elevator e;
    private static Scheduler s;

    @BeforeAll
    public static void init() {
        s = new Scheduler(true);
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
    
}

