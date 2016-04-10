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
	 * Interrupts current execution of the run. Every child of {@link State}
	 * should implement some way of interrupting the computing thread.
	 */
	public void interrupt();

}
