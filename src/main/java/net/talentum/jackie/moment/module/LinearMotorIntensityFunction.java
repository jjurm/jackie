package net.talentum.jackie.moment.module;

import static net.talentum.jackie.tools.MathTools.toRange;
import org.apache.commons.lang3.tuple.ImmutablePair;

public class LinearMotorIntensityFunction implements MotorIntensityFunction {

	@Override
	public ImmutablePair<Integer, Integer> getMotors(double heading) {
		int l, r;

		double linearCoefficient = 1.7;

		l = (int) Math.round(toRange(0.3 + linearCoefficient * heading, -1, 1) * 90);
		r = (int) Math.round(toRange(0.3 - linearCoefficient * heading, -1, 1) * 90);

		return new ImmutablePair<Integer, Integer>(l, r);

	}

}
