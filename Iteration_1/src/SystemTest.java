import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is the JUnit test case for the floor subsystem in the elevator system
 */
class FloorTest {
    Scheduler sche = new Scheduler();
    Floor flo = new Floor(sche, "input");

    /**
     * This test is to test if the floor subsystem is able to read an input file or not
     * @throws InterruptedException
     */
    @Test
    void TestFloor() throws InterruptedException {
        Thread floor = new Thread(flo);
        floor.start();
        Thread.sleep(1000);
        assertEquals(true,flo.doneReading());
    }
}

/**
 * This is the JUnit test case for the Scheduler in the Elevator system
 */
class SchedulerTest {
    Scheduler sche = new Scheduler();
    Floor flo = new Floor(sche, "inputTest");

    /**
     * This test is for the checkRequest() method in the scheduler class
     * @throws InterruptedException
     */
    @Test
    void TestSchedulerCheckRequest() throws InterruptedException {
        Thread floor = new Thread(flo);
        floor.start();
        Thread.sleep(1000);
        List<Request> test = sche.checkRequest(1,Direction.UP);
        assertEquals(7,test.get(0).getDestFloor());
        assertEquals(1,test.get(0).getSourceFloor());
    }

    /**
     * This test is for the getAvailRequest() method in the Scheduler class
     * @throws InterruptedException in case the Thread is interrupted
     */
    @Test
    void TestSchedulerAvaiRequest() throws InterruptedException {
        Thread floor = new Thread(flo);
        floor.start();
        Thread.sleep(1000);
        List<Request> test2 = sche.getAvailRequest();
        assertEquals(1,test2.get(0).getSourceFloor());
        assertEquals(7,test2.get(0).getDestFloor());
    }

    /**
     * This test is for the addToServiceQueue() method in the Scheduler class
     * @throws InterruptedException in case the Thread is interrupted
     */
    @Test
    void TestSchedulerAddServQue() throws InterruptedException {
        Thread floor = new Thread(flo);
        floor.start();
        Thread.sleep(1000);
        sche.addToServiceQueue(3,4,Direction.UP);
        List<Request> test = sche.checkRequest(3,Direction.UP);
        assertEquals(4,test.get(0).getDestFloor());
        assertEquals(3,test.get(0).getSourceFloor());
    }
}

/**
 * This is the JUnit test case for the Elevator subsystem in the Elevator system
 */
class ElevatorTest {
    Scheduler sche = new Scheduler();
    Elevator ele = new Elevator(0, sche, 7);

    /**
     * This test is to test for the simulation of movement using the simMovement() method in the elevator class
     * @throws InterruptedException in case the Thread is interrupted
     */
    @Test
    void TestSimMov() throws InterruptedException {
        Thread elevator = new Thread(ele);
        elevator.start();
        Thread.sleep(1000);
        //Thread.currentThread().interrupt();
        ele.simMovement(Direction.UP);
        assertEquals(2,ele.getCurrentFloor());
        ele.simMovement(Direction.UP);
        assertEquals(3,ele.getCurrentFloor());
        ele.simMovement(Direction.DOWN);
        assertEquals(2,ele.getCurrentFloor());
    }

}