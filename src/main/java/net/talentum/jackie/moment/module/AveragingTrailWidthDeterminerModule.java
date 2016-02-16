package net.talentum.jackie.moment.module;

import java.awt.Point;
import java.util.Deque;
import java.util.LinkedList;

import net.talentum.jackie.moment.MomentData;

public class AveragingTrailWidthDeterminerModule implements TrailBordersMonitorModule {

	MomentData d;
	int recentCount;
	Deque<Point> bordersL = new LinkedList<Point>();
	Deque<Point> bordersR = new LinkedList<Point>();
	Deque<Double> borderDistances = new LinkedList<Double>();

	int widthSum = 0;
	Point borderLSum = new Point();
	Point borderRSum = new Point();
	int actualCount = 0;

	public AveragingTrailWidthDeterminerModule(MomentData d, int recentCount) {
		this.d = d;
		this.recentCount = recentCount;
	}

	@Override
	public double getTrailWidth() {
		// return borderDistances.stream().collect(Collectors.averagingDouble(e -> e));
		return ((double) widthSum) / actualCount;
	}

	@Override
	public Point getBorderL() {
		return getBorder(borderLSum);
	}

	@Override
	public Point getBorderR() {
		return getBorder(borderRSum);
	}

	protected Point getBorder(Point sum) {
		if (actualCount == 0) return null;
		Point avg = new Point(sum.x / actualCount, sum.y / actualCount);
		return d.move(avg, d.mDirectionManager.getDirection(), ((double) recentCount - 1) / 2 * d.param.movedst);
	}

	@Override
	public void registerBorders(Point l, Point r) {
		double width = l.distance(r);
		Point p;

		bordersL.addFirst(l);
		bordersR.addFirst(r);
		borderDistances.addFirst(width);

		widthSum += width;
		borderLSum.translate(l.x, l.y);
		borderRSum.translate(r.x, r.y);

		actualCount++;
		while (actualCount >= recentCount) {
			widthSum -= borderDistances.removeLast();
			p = bordersL.removeLast();
			borderLSum.translate(-p.x, -p.y);
			p = bordersR.removeLast();
			borderRSum.translate(-p.x, -p.y);
			actualCount--;
		}
	}

}
