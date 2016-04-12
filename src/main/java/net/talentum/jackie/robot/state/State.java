package net.talentum.jackie.robot.state;

/**
 * A class that represents a robot's state - some situation with specific
 * behavior. The {@link #run()} method is called repeatedly and infinitely. It
 * should do whatever is needed, e.g. setting motors, controlling other servos
 * etc.
 * 
 * @author padr31
 */
public interface State {

	/**
	 * One run of the state method.
	 * 
	 * @throws InterruptedExecution
	 *             thrown in case when the execution should be interrupted
	 */
	public void run() throws InterruptedExecution;

	/**
	 * This method is called on the active state once, before the first run,
	 * after each switching.
	 */
	public void begin();

	/**
	 * This method is called on the active state once, just before being
	 * switched with another state.
	 */
	public void end();

	/**
	 * Interrupts current execution of the run. Every child of {@link State}
	 * should implement some way of interrupting the computing thread.
	 */
	public void interrupt();

}
