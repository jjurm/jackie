package net.talentum.jackie.robot.state;

import org.apache.commons.lang3.tuple.ImmutablePair;

import net.talentum.jackie.tools.MathTools;

public class ObstacleAvoidanceState implements State {

	long start;

	public ObstacleAvoidanceState() {
		start = System.currentTimeMillis();
	}

	@Override
	public ImmutablePair<Integer, Integer> getMotorInstructions() {

		long passed = System.currentTimeMillis() - start;

		// just an example
		if (MathTools.isBetween(passed, 0, 100)) {
			return new ImmutablePair<Integer, Integer>(100, 100);
		} else {
			return new ImmutablePair<Integer, Integer>(90, 110);
		}

	}

}
