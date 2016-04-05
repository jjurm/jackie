package net.talentum.jackie.robot;

import java.awt.Point;

/**
 * This class holds:
 * <ul>
 * <li>{@link Moment} that was the initiator of the processing, which led to
 * this instruction</li>
 * <li>{@link MomentData} that was created for processing</li>
 * <li>destination {@link Point}, which specifies the position relative to the
 * robot where the robot should go</li>
 * </ul>
 * 
 * @author JJurM
 */
public class RobotInstruction {

	public Moment moment;
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
	public RobotInstruction(Moment moment, MomentData momentData) {
		this(moment, momentData, null);
	}

	/**
	 * Basic constructor
	 * 
	 * @param moment
	 * @param momentData
	 * @param destination
	 */
	public RobotInstruction(Moment moment, MomentData momentData, Point destination) {
		this.moment = moment;
		this.momentData = momentData;
		this.destination = destination;
	}

}
