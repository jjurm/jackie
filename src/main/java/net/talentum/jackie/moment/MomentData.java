package net.talentum.jackie.moment;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import net.talentum.jackie.moment.module.DirectionManagerModule;
import net.talentum.jackie.moment.module.TrailBordersMonitorModule;

public class MomentData {

	public Moment m;
	public BufferedImage image;
	public SensorData sensorData;
	public Parameters param;

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

	public MomentData(Moment moment, Parameters param) {
		this.m = moment;
		this.image = moment.image;
		this.sensorData = moment.sensorData;
		this.param = param;
	}

	// ===== helper functions =====

	public boolean inBounds(Point point) {
		return point.x >= 0 && point.y >= 0 && point.x < image.getWidth() && point.y < image.getHeight();
	}

	public boolean isTrailPoint(Point p) {
		return bw[p.x][p.y];
	}

	public double dst(Point a, Point b) {
		// return Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2));
		return a.distance(b);
	}

	public Point move(Point base, double angle, double distance) {
		return new Point((int) Math.round(base.x + Math.cos(angle) * distance),
				(int) Math.round(base.y + Math.sin(angle) * distance));
	}

	public Point rotate(Point p, Point center, double angle) {
		return new Point(
				(int) Math.round(center.x + (p.x - center.x) * Math.cos(angle) - (p.y - center.y) * Math.sin(angle)),
				(int) Math.round(center.y + (p.x - center.x) * Math.sin(angle) + (p.y - center.y) * Math.cos(angle)));
	}

	public Point avg(Point... points) {
		int sx = 0, sy = 0;
		for (Point p : points) {
			sx += p.x;
			sy += p.y;
		}
		return new Point(sx / points.length, sy / points.length);
	}

	public double perpendicularAngle(double angle) {
		double perpAngle = angle + Math.PI / 2;
		/*
		 * if (perpAngle < -Math.PI) perpAngle += 2 * Math.PI;
		 */
		return perpAngle;
	}

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
	 * @param base
	 *            where to start
	 * @param direction
	 *            direction to look at
	 * @param dst
	 *            moving distance (step)
	 * @param orientation
	 *            either {@code 1} or {@code -1}
	 * @return
	 */
	public Point findBorder(Point base, double direction, int dst, int orientation) {
		Point p = base;
		Point last = base;
		for (int i = 1; inBounds(p) && isTrailPoint(p); i++) {
			last = p;
			p = move(base, direction, param.movedst * i * orientation);
		}
		return last;
	}

}
