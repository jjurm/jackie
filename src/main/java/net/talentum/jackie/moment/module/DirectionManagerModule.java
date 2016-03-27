package net.talentum.jackie.moment.module;

import java.awt.Point;

import net.talentum.jackie.moment.MomentData;

/**
 * Interface for module that manages direction. It can accept a new line point,
 * whenever one is found, and computes the tendency of the direction of the
 * line.
 * 
 * @author JJurM
 */
public interface DirectionManagerModule {

	/**
	 * Register new line point
	 * 
	 * @param p
	 */
	public void addLinePoint(Point p);

	/**
	 * Returns computed direction
	 * 
	 * @return
	 */
	public double getDirection();

	/**
	 * Forcibly overwrites the computed data with new direction. This is
	 * generally used after angular turns.
	 * 
	 * @param position
	 *            the point of the new start
	 * @param direction
	 *            the new direction
	 * @param weight
	 *            how much weight should be given to the new overwriting points,
	 *            in other words, how hard it will be for following line points
	 *            to affect the computed direction
	 * @param d {@link MomentData}
	 */
	public void overwriteAll(Point position, double direction, double weight, MomentData d);

}
