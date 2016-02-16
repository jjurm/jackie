package net.talentum.jackie.moment.module;

import java.awt.Point;

public interface TrailBordersMonitorModule {

	public double getTrailWidth();

	public Point getBorderL();
	public Point getBorderR();

	public void registerBorders(Point l, Point r);

}
