/**
 * Timer object calculates durations
 * @author Joshua Gatto
 *
 */
public class StopWatch {

	private long startTime;
	private long duration;
	private long endTime;
	private boolean running;
	
	/**
	 * Sole constructor for Timer. Sets it's startTime upon creation (ie: starts the timer).
	 * duration and endTime are left null until a valid value is generated.  
	 */
	public StopWatch() {
		running = true;
		startTime = System.nanoTime();
	}
	
	/**
	 * Stops the timer.
	 * @return duration Length of time spent running.
	 */
	public long end() {
		endTime = System.nanoTime();
		running = false;
		return duration = endTime - startTime;
	}
	
	/**
	 * Gets the startTime.
	 * @return starTime The time at which the Timer was started.
	 */
	public long getStartTime() {
		return startTime;
	}
	
	/**
	 * Resets the startTime to the current time. Resets duration
	 * @return
	 */
	public long start() {
		running = true;
		return startTime = System.nanoTime();
	}
	
	/**
	 * Gets the endTime. If the timer is still running, it will be stopped.
	 * @return endTime The time at which the Timer was stopped.
	 */
	public long getEndTime() {
		if(running) {
			end();
			return endTime;
		}else {
			return endTime;
		}
	}
	
	/**
	 * Gets the duration. If the timer is still running, the duration at that instance will be returned.
	 * @return duration Length of time spent running.
	 */
	public long getDuration() {
		if(running) {
			return System.nanoTime() - startTime;
		}else {
			return duration;
		}
	}
	
	/**
	 * Gets the running status of the Timer.
	 * @return running The status of the Timer.
	 */
	public boolean getRunning() {
		return running;
	}
	
	/**
	 * Resets the values of the timer.
	 */
	public void reset() {
		startTime = 0;
		endTime = 0;
		duration = 0;
	}
}
