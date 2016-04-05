package net.talentum.jackie.module.impl;

import java.awt.Point;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;

import net.talentum.jackie.module.DirectionManagerModule;
import net.talentum.jackie.robot.MomentData;

public class VectorDirectionManagerModule implements DirectionManagerModule {

	protected Deque<Point> points = new LinkedList<Point>();
	protected int number;
	int lastPointAveragingPoints;

	/**
	 * @param number
	 *            Number of recently added line points to keep
	 * @param lastPointAveragingPoints
	 *            number of most-recent points to use for computing average for
	 *            <em>last point</em>
	 */
	public VectorDirectionManagerModule(int number, int lastPointAveragingPoints) {
		this.number = number;
		this.lastPointAveragingPoints = lastPointAveragingPoints;
	}

	@Override
	public void addLinePoint(Point p) {
		points.addFirst(p);
		while(points.size() > number) {
			points.removeLast();
		}
	}

	@Override
	public double getDirection() {
		int dx = 0, dy = 0;
		Point p, lastPoint = null;
		int i = 0;
		boolean firstPhase = true;
		for (Iterator<Point> it = points.iterator(); it.hasNext(); i++) {
			p = it.next();
			if (firstPhase) {
				dx += p.x;
				dy += p.y;
				if ((i + 1) >= 4) {
					lastPoint = new Point(dx / (i + 1), dy / (i + 1));
					i = -1;
					dx = 0;
					dy = 0;
					firstPhase = false;
				}
			} else {
				dx += lastPoint.x - p.x;
				dy += lastPoint.y - p.y;
			}
		}
		return Math.atan2(dy / i, dx / i);
	}

	@Override
	public void overwriteAll(Point position, double direction, double weight, MomentData d) {
		points.clear();
		for (int i = 0; i < number; i++) {
			points.addLast(d.move(position, direction, - d.param.movedst * i * weight));
		}
	}

}
