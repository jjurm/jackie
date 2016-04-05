package net.talentum.jackie.module;

import java.awt.Point;

/**
 * Module interface for monitoring borders of the trail that is being
 * recognized. Dispose of methods for registering new border points and for
 * returning computed expected border points and averaged trail width.
 * 
 * @author JJurM
 */
public interface TrailBordersMonitorModule {

	/**
	 * Returns computed average width of the trail
	 * 
	 * @return
	 */
	public double getTrailWidth();

	/**
	 * Returns next expected point for the left border
	 * 
	 * @return
	 */
	public Point getBorderL();

	/**
	 * Returns next expected point for the right border
	 * 
	 * @return
	 */
	public Point getBorderR();

	/**
	 * Registers new recognized border points.
	 * 
	 * @param l
	 * @param r
	 */
	public void registerBorders(Point l, Point r);

}
