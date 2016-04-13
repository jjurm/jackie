package net.talentum.jackie.robot.strategy;

import java.awt.image.BufferedImage;

import net.talentum.jackie.robot.MomentData;
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

	protected MomentData d;

	/**
	 * Prepares the strategy for processing the given moment. Involves creating
	 * {@link MomentData} bonded to the moment.
	 * 
	 * @param image
	 */
	public void prepare(BufferedImage image) {
		d = new MomentData(image);
	}

	/**
	 * Launches the image processing.
	 * 
	 * @return
	 */
	public abstract RobotInstruction evaluate();

}
