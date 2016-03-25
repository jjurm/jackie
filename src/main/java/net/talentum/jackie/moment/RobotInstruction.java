package net.talentum.jackie.moment;

import java.awt.Point;

public class RobotInstruction {

	public Moment moment;
	public MomentData momentData;
	public Point destination;

	public RobotInstruction(Moment moment, MomentData momentData) {
		this(moment, momentData, null);
	}

	public RobotInstruction(Moment moment, MomentData momentData, Point destination) {
		this.moment = moment;
		this.momentData = momentData;
		this.destination = destination;
	}

}
