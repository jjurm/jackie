package net.talentum.jackie.robot.state;

/**
 * An example state that does nothing
 * 
 * @author JJurM
 */
public class NullState implements State {

	@Override
	public void run() {

	}

	@Override
	public void interrupt() {
		// no need for interrupting
	}

}
