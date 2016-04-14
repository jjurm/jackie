package net.talentum.jackie.module.impl;

import java.util.function.Function;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.lang3.tuple.ImmutablePair;

import net.talentum.jackie.module.MotorIntensityFunction;
import net.talentum.jackie.system.ConfigurationManager;

public class SimpleMotorIntensityFunction implements MotorIntensityFunction {

	@Override
	public ImmutablePair<Integer, Integer> getMotors(double heading) {
		HierarchicalConfiguration config = ConfigurationManager.getGeneralConfiguration();
		String baseString = "params/lineFollowing/motorIntensityFunction/";
		int l, r;
		
		double baseSpeed = config.getDouble(baseString + "baseSpeed");
		double coefficient = config.getDouble(baseString + "coefficient");
		
		Function<Integer, Integer> f = new Function<Integer, Integer>() {
			@Override
			public Integer apply(Integer motor) {
				double value = baseSpeed + coefficient * heading * motor;
				return (int) (Math.round(value * 90));
			}
		};
		
		l = f.apply(1);
		r = f.apply(-1);

		return new ImmutablePair<Integer, Integer>(l, r);
	}

}
