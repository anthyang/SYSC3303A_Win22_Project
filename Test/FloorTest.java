import static org.junit.jupiter.api.Assertions.*;

import java.net.SocketException;

import org.junit.jupiter.api.*;

/**
 * This is the JUnit test case for the floor subsystem in the elevator system
 */
class FloorTest {
    private static Floor f;
    
    @BeforeAll
    public static void init() {
    	try {
			f = new Floor("test/inputTest");
		} catch (SocketException e) {
			e.printStackTrace();
		}
    }

    /**
     * This test is to test if the floor subsystem is able to read an input file or not
     */
    @Test
    public void TestFloor() throws InterruptedException {
        Thread floor = new Thread(f);
        floor.start();
        Thread.sleep(1000);
        assertTrue(f.doneReading());
    }
    
    /**
     * Asserts that a request for the specified floor is placed into the schedulers master queue.
     */
    @Test
    public void testRequestElevator() {
    	f.requestElevator(0, 4, Direction.UP);
    	Scheduler s = new Scheduler(False);
    	Request r = s.getMasterQueue().poll();
    	assertEquals(r.getSourceFloor(), 0);
    	assertEquals(r.getDestFloor(), 4);
    	assertEquals(r.getDirection(), Direction.UP);
    }
    
    /**
     * Asserts that the requests in the input file are placed into the schedulers master queue.
     */
    @Test
    public void testReadInputFile() {
    	f.run();
    	if(f.doneReading()) {
    		Scheduler s = new Scheduler(False);
        	Request r = s.getMasterQueue().poll();
        	assertEquals(r.getSourceFloor(), 2);
        	assertEquals(r.getDestFloor(), 4);
        	assertEquals(r.getDirection(), Direction.UP);
        	r = s.getMasterQueue().poll();
        	assertEquals(r.getSourceFloor(), 1);
        	assertEquals(r.getDestFloor(), 4);
        	assertEquals(r.getDirection(), Direction.UP);
        	r = s.getMasterQueue().poll();
        	assertEquals(r.getSourceFloor(), 4);
        	assertEquals(r.getDestFloor(), 1);
        	assertEquals(r.getDirection(), Direction.DOWN);
    	}
    }
}
