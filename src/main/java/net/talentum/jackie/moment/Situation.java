package net.talentum.jackie.moment;

import java.awt.Point;

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
