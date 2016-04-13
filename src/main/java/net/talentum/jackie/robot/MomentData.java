package net.talentum.jackie.robot;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import net.talentum.jackie.module.DirectionManagerModule;
import net.talentum.jackie.module.TrailBordersMonitorModule;
import net.talentum.jackie.system.Config;

/**
 * This object is bonded to {@link Moment}. Contains data, some may be only
 * temporary, used for evaluation by strategies. Each strategy can use the
 * variables in a different way. MomentData is created by strategies, just
 * before the moment processing.
 * 
 * @author JJurM
 */
public class MomentData {

	public BufferedImage image;
	public SensorData sensorData;

	public boolean[][] bw;

	public List<Point> line = new ArrayList<Point>();
	public List<Point> bordersL = new ArrayList<Point>();
	public List<Point> bordersR = new ArrayList<Point>();
	public List<Double> directionList = new ArrayList<Double>();
	public List<Point> notFound = new ArrayList<Point>();
	public List<Point> highlight = new ArrayList<Point>();

	public Point position;
	public int notFoundCount = 0;

	public DirectionManagerModule mDirectionManager;
	public TrailBordersMonitorModule mTrailBordersMonitor;

	public MomentData(BufferedImage image) {
		this.image = image;
	}

	// ===== helper functions =====

	/**
	 * Checks if the given point is in bounds of the image.
	 * 
	 * @param point
	 * @return {@code true}, if the point is in bounds
	 */
	public boolean inBounds(Point point) {
		return point.x >= 0 && point.y >= 0 && point.x < image.getWidth() && point.y < image.getHeight();
	}

	/**
	 * Checks if the given point is "black", i.e. if it was determined as a
	 * trail point.
	 * 
	 * @param p
	 * @return
	 */
	public boolean isTrailPoint(Point p) {
		return bw[p.x][p.y];
	}

	/**
	 * Calculates distance between the two points.
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public double dst(Point a, Point b) {
		// return Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2));
		return a.distance(b);
	}

	/**
	 * Moves the given base point in a specified angle, by a specified distance.
	 * 
	 * @param base
	 *            where to start
	 * @param angle
	 *            direction to move the point
	 * @param distance
	 *            distance to move the point
	 * @return the new point
	 */
	public Point move(Point base, double angle, double distance) {
		return new Point((int) Math.round(base.x + Math.cos(angle) * distance),
				(int) Math.round(base.y + Math.sin(angle) * distance));
	}

	/**
	 * Rotates one point about another.
	 * 
	 * @param p
	 *            the point to be rotated
	 * @param center
	 *            the center of the rotation
	 * @param angle
	 *            the angle by which to rotate
	 * @return the new point
	 */
	public Point rotate(Point p, Point center, double angle) {
		return new Point(
				(int) Math.round(center.x + (p.x - center.x) * Math.cos(angle) - (p.y - center.y) * Math.sin(angle)),
				(int) Math.round(center.y + (p.x - center.x) * Math.sin(angle) + (p.y - center.y) * Math.cos(angle)));
	}

	/**
	 * Computes arithmetic average of the given points. Each has the same
	 * weight.
	 * 
	 * @param points
	 *            list of points
	 * @return the average point
	 */
	public Point avg(Point... points) {
		int sx = 0, sy = 0;
		for (Point p : points) {
			sx += p.x;
			sy += p.y;
		}
		return new Point(sx / points.length, sy / points.length);
	}

	public Point weightedAvg(Point a, Point b, double weightA) {
		int sx = 0, sy = 0;
		sx += a.x * weightA;
		sy += a.y * weightA;
		sx += b.x * (1 - weightA);
		sy += b.y * (1 - weightA);
		return new Point(sx, sy);
	}

	/**
	 * Returns an angle that is perpendicular to the given angle.
	 * 
	 * @param angle
	 * @return new angle
	 */
	public double perpendicularAngle(double angle) {
		return angle + Math.PI / 2;
	}

	/**
	 * Finds point, on a straight line, that is specified by being or not being
	 * a trail point and is the nearest such point to the specified {@code base}
	 * point.
	 * 
	 * @param base
	 *            base point, from which the distance is calculated and compared
	 * @param angle
	 *            angle determining the line, across which to find the point
	 * @param findTrail
	 *            whether the wanted point is or isn't trail point
	 * @param dst
	 *            distance interval between adjacent points to be checked
	 *            (greater interval causes the algorithm to search faster, but
	 *            the inaccuracy of the returned point is also greater)
	 * @param maxdst
	 *            maximum distance to search for; {@code null} is returned if no
	 *            such point is found in the specified distance
	 * @return
	 */
	public Point findLinearlyNearestPoint(Point base, double angle, boolean findTrail, int dst, int maxdst) {
		// returns Point or null
		boolean wasInBounds = true;
		Point p = base;
		for (int i = 1; wasInBounds && dst(base, p) <= maxdst; i++) {
			wasInBounds = false;
			for (int j = 1; j >= -1; j -= 2) {
				p = move(base, angle, dst * i * j);
				if (inBounds(p)) {
					wasInBounds = true;
					if (isTrailPoint(p) == findTrail) {
						if (findTrail) {
							return p;
						} else {
							return move(base, angle, dst * Math.max(i - 1, 0) * j);
						}
					}
				}
			}
		}
		return null;
	}

	/**
	 * Finds border of the trail, provided that the {@code base} point lies on
	 * the trail.
	 * 
	 * @param base
	 *            where to start
	 * @param direction
	 *            direction to look at
	 * @param dst
	 *            moving distance (step)
	 * @param orientation
	 *            either {@code 1} or {@code -1}
	 * @return last point that was determined as a trail point; {@code base} can
	 *         also be returned, if no further trail point was found
	 */
	public Point findBorder(Point base, double direction, int dst, int orientation) {
		Point p = base;
		Point last = base;
		for (int i = 1; inBounds(p) && isTrailPoint(p); i++) {
			last = p;
			p = move(base, direction, Config.movedst * i * orientation);
		}
		return last;
	}

}
