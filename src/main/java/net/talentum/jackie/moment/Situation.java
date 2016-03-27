package net.talentum.jackie.moment;

import java.awt.Point;

/**
 * Situation consists of a point and a heading direction. Can be used
 * universally, e.g. in line recognition strategies or as the robot's position
 * relative to some global coordinate system.
 * 
 * @author JJurM
 */
public class Situation {

	private Point point;
	double direction;

	public Situation(Point point, double direction) {
		this.point = point;
		this.direction = direction;
	}

	public Point getPoint() {
		return point;
	}

	public double getDirection() {
		return direction;
	}

}
