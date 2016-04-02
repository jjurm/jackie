package net.talentum.jackie.moment.module;

import java.util.function.Function;

import org.apache.commons.lang3.tuple.ImmutablePair;

import net.talentum.jackie.tools.MathTools;

public class LinearMotorIntensityFunction implements MotorIntensityFunction {

	@Override
	public ImmutablePair<Integer, Integer> getMotors(double heading) {
		int l, r;

		double coefficient = 0.3;
		double positiveMotorExponent = 2.0;
		double negativeMotorExponent = 3.0;

		double base = 0.1;

		Function<Integer, Integer> f = new Function<Integer, Integer>() {
			@Override
			public Integer apply(Integer motor) {
				boolean positiveMotor = MathTools.side(heading) == motor;
				double exponent = positiveMotor ? positiveMotorExponent : negativeMotorExponent;
				double value = MathTools.toRange(
						base + motor * coefficient * Math.pow(Math.abs(heading), exponent)
						* MathTools.side(heading) * (positiveMotor ? 0.7 : 1),
						-1, 1);
				return (int) Math.round(value) * 90 + 90;
			}
		};

		l = f.apply(1);
		r = f.apply(-1);

		return new ImmutablePair<Integer, Integer>(l, r);

	}

}
