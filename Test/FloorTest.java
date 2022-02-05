import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is the JUnit test case for the floor subsystem in the elevator system
 */
class FloorTest {
    Scheduler sche = new Scheduler();
    Floor flo = new Floor(sche, "Test/inputTest");

    /**
     * This test is to test if the floor subsystem is able to read an input file or not
     *
     * @throws InterruptedException
     */
    @Test
    void TestFloor() throws InterruptedException {
        Thread floor = new Thread(flo);
        floor.start();
        Thread.sleep(1000);
        assertEquals(true, flo.doneReading());
    }
}
