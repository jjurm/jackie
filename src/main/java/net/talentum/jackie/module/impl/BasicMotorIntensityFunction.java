package net.talentum.jackie.module.impl;

import java.util.function.Function;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.lang3.tuple.ImmutablePair;

import net.talentum.jackie.module.MotorIntensityFunction;
import net.talentum.jackie.system.ConfigurationManager;
import net.talentum.jackie.tools.MathTools;

public class BasicMotorIntensityFunction implements MotorIntensityFunction {

	@Override
	public ImmutablePair<Integer, Integer> getMotors(double heading) {
		HierarchicalConfiguration config = ConfigurationManager.getGeneralConfiguration();
		String baseString = "params/lineFollowing/motorIntensityFunction/";
		int l, r;

		double baseSpeed = config.getDouble(baseString + "baseSpeed");
		double decelerateExponent = config.getDouble(baseString + "decelerateExponent");
		double decelerateCoefficient = config.getDouble(baseString + "decelerateCoefficient");
		double internalCoefficient = config.getDouble(baseString + "internalCoefficient");
		double coefficient = config.getDouble(baseString + "coefficient");
		double positiveMotorExponent = config.getDouble(baseString + "positiveMotorExponent");
		double negativeMotorExponent = config.getDouble(baseString + "negativeMotorExponent");
		double positiveMotorCoefficient = config.getDouble(baseString + "positiveMotorCoefficient");
		
		Function<Integer, Integer> f = new Function<Integer, Integer>() {
			@Override
			public Integer apply(Integer motor) {
				boolean positiveMotor = MathTools.side(heading) == motor;
				double exponent = positiveMotor ? positiveMotorExponent : negativeMotorExponent;
				double value = MathTools.toRange(
						baseSpeed
						- Math.pow(Math.abs(heading * internalCoefficient), decelerateExponent) * decelerateCoefficient
						+ Math.pow(Math.abs(heading * internalCoefficient), exponent) * motor * coefficient
						* MathTools.side(heading) * (positiveMotor ? positiveMotorCoefficient : 1),
						-1, 1);
				return (int) (Math.round(value * 90));
			}
		};

		l = f.apply(1);
		r = f.apply(-1);

		return new ImmutablePair<Integer, Integer>(l, r);

	}

}
