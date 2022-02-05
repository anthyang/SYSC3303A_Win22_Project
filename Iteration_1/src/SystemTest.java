import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FloorTest {
    Scheduler sche = new Scheduler();
    Floor flo = new Floor(sche, "input");
    @Test
    void TestFloor() throws InterruptedException {
        Thread floor = new Thread(flo);
        floor.start();
        Thread.sleep(1000);
        assertEquals(true,flo.doneReading());
    }
}

class SchedulerTest {
    Scheduler sche = new Scheduler();
    Floor flo = new Floor(sche, "inputTest");
    @Test
    void TestSchedulerCheckRequest() throws InterruptedException {
        Thread floor = new Thread(flo);
        floor.start();
        Thread.sleep(1000);
        List<Request> test = sche.checkRequest(1,Direction.UP);
        assertEquals(7,test.get(0).getDestFloor());
        assertEquals(1,test.get(0).getSourceFloor());
    }
    @Test
    void TestSchedulerAvaiRequest() throws InterruptedException {
        Thread floor = new Thread(flo);
        floor.start();
        Thread.sleep(1000);
        List<Request> test2 = sche.getAvailRequest();
        assertEquals(1,test2.get(0).getSourceFloor());
        assertEquals(7,test2.get(0).getDestFloor());
    }
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

class ElevatorTest {
    Scheduler sche = new Scheduler();
    Elevator ele = new Elevator(0, sche, 7);
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