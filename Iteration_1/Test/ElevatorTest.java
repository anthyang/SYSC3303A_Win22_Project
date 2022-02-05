import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is the JUnit test case for the Elevator subsystem in the Elevator system
 */
class ElevatorTest {
    Scheduler sche = new Scheduler();
    Elevator ele = new Elevator(0, sche, 7);

    /**
     * This test is to test for the simulation of movement using the simMovement() method in the elevator class
     *
     * @throws InterruptedException in case the Thread is interrupted
     */
    @Test
    void TestSimMov() throws InterruptedException {
        Thread elevator = new Thread(ele);
        elevator.start();
        Thread.sleep(1000);
        //Thread.currentThread().interrupt();
        ele.simMovement(Direction.UP);
        assertEquals(2, ele.getCurrentFloor());
        ele.simMovement(Direction.UP);
        assertEquals(3, ele.getCurrentFloor());
        ele.simMovement(Direction.DOWN);
        assertEquals(2, ele.getCurrentFloor());
    }

}
