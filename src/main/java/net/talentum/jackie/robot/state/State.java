package net.talentum.jackie.robot.state;

/**
 * A class that represents a robot's state - some situation with specific
 * behavior. The {@link #run()} method is called repeatedly and infinitely. It
 * should do whatever is needed, e.g. setting motors, controlling other servos
 * etc.
 * 
 * @author padr31
 */
public interface State extends Runnable {

	@Override
	public void run();

}
