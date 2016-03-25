package net.talentum.jackie.moment.module;

import org.apache.commons.lang3.tuple.ImmutablePair;

public interface MotorIntensityFunction {

	ImmutablePair<Integer, Integer> getMotors(double heading);
	
}
