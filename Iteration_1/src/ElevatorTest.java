import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

class AllTest {

    Scheduler sche = new Scheduler();
    Elevator ele = new Elevator(0, sche, 7);
    Floor flo = new Floor(sche, "Input");
    @Test
    void run() {
    }

    @Test
    void simMovement() {
    }

    @Test
    void moveElevator() {
    }

    @Test
    public void testElevator() throws InterruptedException {
        int numThreads = 2;
        ExecutorService service = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(numThreads);

    }
}