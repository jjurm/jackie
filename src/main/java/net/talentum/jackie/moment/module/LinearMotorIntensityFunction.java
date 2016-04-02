package net.talentum.jackie.moment.module;

import java.util.function.Function;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.lang3.tuple.ImmutablePair;

import net.talentum.jackie.system.ConfigurationManager;
import net.talentum.jackie.tools.MathTools;

public class LinearMotorIntensityFunction implements MotorIntensityFunction {

	@Override
	public ImmutablePair<Integer, Integer> getMotors(double heading) {
		HierarchicalConfiguration config = ConfigurationManager.getGeneralConfiguration();
		String baseString = "params/lineFollowing/motorIntensityFunction/";
		int l, r;

		double baseSpeed = config.getDouble(baseString + "baseSpeed");

		double coefficient = config.getDouble(baseString + "coefficient");
		double positiveMotorExponent = config.getDouble(baseString + "positiveMotorExponent");
		double negativeMotorExponent = config.getDouble(baseString + "negativeMotorExponent");

		Function<Integer, Integer> f = new Function<Integer, Integer>() {
			@Override
			public Integer apply(Integer motor) {
				boolean positiveMotor = MathTools.side(heading) == motor;
				double exponent = positiveMotor ? positiveMotorExponent : negativeMotorExponent;
				double value = MathTools.toRange(
						baseSpeed + motor * coefficient * Math.pow(Math.abs(heading), exponent)
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
