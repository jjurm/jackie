package net.talentum.jackie.state;

import org.apache.commons.lang3.tuple.ImmutablePair;

public interface State {

	public ImmutablePair<Integer, Integer> getMotorInstructions();

}
