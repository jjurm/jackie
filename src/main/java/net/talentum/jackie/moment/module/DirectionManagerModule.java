package net.talentum.jackie.moment.module;

import java.awt.Point;

import net.talentum.jackie.moment.MomentData;

public interface DirectionManagerModule {

	public void addLinePoint(Point p);

	public double getDirection();

	public void overwriteAll(Point position, double direction, double weight, MomentData d);

}
