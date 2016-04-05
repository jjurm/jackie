package net.talentum.jackie.module;

import org.apache.commons.lang3.tuple.ImmutablePair;

/**
 * Interface for a function that accepts wanted heading of the robot and returns
 * the desired intensity for motors.
 * 
 * @author JJurM
 */
public interface MotorIntensityFunction {

	/**
	 * Return values for individual motors (left and right)
	 * 
	 * @param heading
	 * @return the left (first) number corresponds to the left motor; the right
	 *         (second) number corresponds to the right motor
	 */
	ImmutablePair<Integer, Integer> getMotors(double heading);

}
