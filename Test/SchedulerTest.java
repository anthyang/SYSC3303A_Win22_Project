import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * This is the JUnit test case for the Scheduler in the Elevator system
 */
class SchedulerTest {
    private Elevator e;
    private Scheduler sche;

    @BeforeEach
    public void setUp() {
        sche = new Scheduler();
        sche.addExternalRequest(new Request(2, 5, Direction.UP));
        sche.addExternalRequest(new Request(1, 7, Direction.UP));
        e = new Elevator(1, sche, Config.NUMBER_OF_FLOORS);
    }

    /**
     * This test is for the updateQueue() method in the scheduler class
     */
    @Test
    public void testSchedulerUpdateQueueCurrFloor() {
        List<Request> test = sche.updateQueue(e, 1);
        // Should give request from 1 to 7 (current floor)
        Assertions.assertEquals(7, test.get(0).getDestFloor());
        Assertions.assertEquals(1, test.get(0).getSourceFloor());
    }

    /**
     * This test is for the updateQueue() method in the Scheduler class
     */
    @Test
    public void testSchedulerUpdateQueueFIFO() {
        Elevator e = new Elevator(2, sche, Config.NUMBER_OF_FLOORS);
        List<Request> test2 = sche.updateQueue(e, 7);
        // Should give request from 2 to 5 (FIFO)
        Assertions.assertEquals(2, test2.get(0).getSourceFloor());
        Assertions.assertEquals(5, test2.get(0).getDestFloor());
    }

    /**
     * This test is for the updateQueue() method in the Scheduler class
     */
    @Test
    public void testSchedulerUpdateQueueDone() {
        Scheduler scheduler = new Scheduler();
        scheduler.endActions();
        Elevator e = new Elevator(2, scheduler, Config.NUMBER_OF_FLOORS);
        // No requests, should be null
        List<Request> test = scheduler.updateQueue(e, 1);
        Assertions.assertNull(test);
    }

    @Test
    public void testSchedulerServiceComplete() {
        sche.updateQueue(e, 2);
        sche.serviceComplete(e, 5);
        sche.updateQueue(e, 1);
        sche.serviceComplete(e, 7);
        sche.endActions();
        Assertions.assertNull(sche.updateQueue(e, 7));
    }
}

