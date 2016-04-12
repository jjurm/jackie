package net.talentum.jackie.robot.state;

import net.talentum.jackie.robot.Robot;

/**
 * Abstract {@link State} implementation with helper methods to interrupt the
 * execution and to check if the execution should continue.
 * 
 * @author JJurM
 */
public abstract class AbstractState implements State {

	Thread thread;
	protected Robot robot;

	/**
	 * Default constructor
	 * 
	 * @param robot
	 */
	public AbstractState(Robot robot) {
		this.robot = robot;
	}

	@Override
	public final void run() {
		thread = Thread.currentThread();
		run0();
	}

	/**
	 * Internal method called from outside the run method.
	 */
	public abstract void run0();

	@Override
	public void interrupt() {
		thread.interrupt();
	}

	/**
	 * Checks if the execution should be interrupted.
	 * 
	 * @throws InterruptedExecution
	 */
	public void checkInterrupted() throws InterruptedExecution {
		if (!robot.run.get()) {
			throw new InterruptedExecution();
		}
	}
	
	@Override
	public void begin() {
		// do nothing as default
	}
	
	@Override
	public void end() {
		// do nothing as default
	}

}
