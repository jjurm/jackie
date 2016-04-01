package net.talentum.jackie.moment.module;

import static net.talentum.jackie.tools.MathTools.toRange;
import org.apache.commons.lang3.tuple.ImmutablePair;

public class LinearMotorIntensityFunction implements MotorIntensityFunction {

	@Override
	public ImmutablePair<Integer, Integer> getMotors(double heading) {
		int l, r;

		double linearCoefficient = 0.3;

		double base = 0.2;
		
		l = (int) Math.round(toRange(base + linearCoefficient * heading, -1, 1) * 90) + 90;
		r = (int) Math.round(toRange(base - linearCoefficient * heading, -1, 1) * 90) + 90;

		return new ImmutablePair<Integer, Integer>(l, r);

	}

}
