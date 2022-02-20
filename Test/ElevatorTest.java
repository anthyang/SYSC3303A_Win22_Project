import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * This is the JUnit test case for the Elevator subsystem in the Elevator system
 */
class ElevatorTest {
    private Scheduler sche;

    @BeforeEach
    public void setUp() {
        sche = new Scheduler();
        sche.addExternalRequest(new Request(2, 1, Direction.UP));
    }

    /**
     * This test is to test for the simulation of movement using the simMovement() method in the elevator class
     */
    @Test
    public void testSimMov() {
        Elevator ele = new Elevator(0, sche, 7);
        ele.simMovement(Direction.UP);
        assertEquals(2, ele.getCurrentFloor());
        ele.simMovement(Direction.UP);
        assertEquals(3, ele.getCurrentFloor());
        ele.simMovement(Direction.DOWN);
        assertEquals(2, ele.getCurrentFloor());
    }

}
