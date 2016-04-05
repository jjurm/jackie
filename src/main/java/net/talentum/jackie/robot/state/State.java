package net.talentum.jackie.robot.state;

import org.apache.commons.lang3.tuple.ImmutablePair;

public interface State {

	public ImmutablePair<Integer, Integer> getMotorInstructions();

}
