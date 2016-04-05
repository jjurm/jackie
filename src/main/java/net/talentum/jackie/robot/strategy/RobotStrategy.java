package net.talentum.jackie.robot.strategy;

import net.talentum.jackie.robot.Moment;
import net.talentum.jackie.robot.MomentData;
import net.talentum.jackie.robot.Parameters;
import net.talentum.jackie.robot.RobotInstruction;

/**
 * Abstract class for image recognizing strategies. Takes {@link Moment} and
 * returns {@link RobotInstruction}.
 * 
 * <p>
 * At first, {@link #prepare(Moment)} must be called, then {@link #evaluate()}.
 * </p>
 * 
 * @author JJurM
 */
public abstract class RobotStrategy {

	protected Parameters param;
	protected MomentData d;

	public RobotStrategy(Parameters param) {
		this.param = param;
	}

	/**
	 * Prepares the strategy for processing the given moment. Involves creating
	 * {@link MomentData} bonded to the moment.
	 * 
	 * @param moment
	 */
	public void prepare(Moment moment) {
		d = new MomentData(moment, param);
	}

	/**
	 * Launches the image processing.
	 * 
	 * @return
	 */
	public abstract RobotInstruction evaluate();

}
