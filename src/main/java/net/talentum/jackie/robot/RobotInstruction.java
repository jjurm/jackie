package net.talentum.jackie.robot;

import java.awt.Point;
import java.awt.image.BufferedImage;

/**
 * This class holds:
 * <ul>
 * <li>Image that was the initiator of the processing, which led to this
 * instruction</li>
 * <li>{@link MomentData} that was created for processing</li>
 * <li>destination {@link Point}, which specifies the position relative to the
 * robot where the robot should go</li>
 * </ul>
 * 
 * @author JJurM
 */
public class RobotInstruction {

	public BufferedImage image;
	public MomentData momentData;
	public Point destination;

	/**
	 * Alternative constructor with no {@code destination} specified. Can be
	 * used for example in cases where the image recognition has been
	 * unsuccessful.
	 * 
	 * @param moment
	 * @param momentData
	 */
	public RobotInstruction(BufferedImage image, MomentData momentData) {
		this(image, momentData, null);
	}

	/**
	 * Basic constructor
	 * 
	 * @param moment
	 * @param momentData
	 * @param destination
	 */
	public RobotInstruction(BufferedImage image, MomentData momentData, Point destination) {
		this.image = image;
		this.momentData = momentData;
		this.destination = destination;
	}

}
