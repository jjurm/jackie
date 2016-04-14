package net.talentum.jackie.robot.strategy;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;

import net.talentum.jackie.module.BooleanImageFilterModule;
import net.talentum.jackie.module.BorderFinderModule;
import net.talentum.jackie.module.ImageModifierModule;
import net.talentum.jackie.module.IntersectionSolver;
import net.talentum.jackie.robot.MomentData;
import net.talentum.jackie.robot.RobotInstruction;
import net.talentum.jackie.system.Config;
import net.talentum.jackie.tools.InstructionPainter;
import net.talentum.jackie.tools.Interval;
import net.talentum.jackie.tools.MathTools;

/**
 * Very simple and fast strategy that works similarly to a reflectance sensor
 * array (line detection sensor). It simply looks at pre-defined straight lines
 * across the image. The main line, which simulates the reflectance sensor
 * array, is positioned horizontally across the image.
 * 
 * @author JJurM
 */
public class HorizontalLevelObservingStrategy extends RobotStrategy {

	ImageModifierModule mImageModifier;
	BooleanImageFilterModule mBooleanImageFilter;
	BorderFinderModule mBorderFinder;
	IntersectionSolver mIntersectionSolver;
	
	long observeTopMillis;
	RobotInstruction lastInstruction;
	long lastInstructionUntil;

	public HorizontalLevelObservingStrategy(ImageModifierModule mImageModifier,
			BooleanImageFilterModule mBooleanImageFilter, BorderFinderModule mBorderFinder,
			IntersectionSolver mIntersectionSolver) {
		this.mImageModifier = mImageModifier;
		this.mBooleanImageFilter = mBooleanImageFilter;
		this.mBorderFinder = mBorderFinder;
		this.mIntersectionSolver = mIntersectionSolver;
	}

	@Override
	public RobotInstruction evaluate() {
		return evaluateA();
	}

	@Override
	public void prepare(BufferedImage image) {
		super.prepare(image);

		// process image
		if (mImageModifier != null)
			d.image = mImageModifier.modify(d.image);

		// create boolean array
		d.bw = mBooleanImageFilter.filter(d.image);
	}

	public RobotInstruction evaluateA() {
		if (System.currentTimeMillis() < lastInstructionUntil) {
			System.out.println("Skipping");
			return lastInstruction;
		}
		
		// find line
		int x = d.image.getWidth() / 2;
		int y = d.image.getHeight() / 2;

		Triple<Point, Point, Point> top = checkLine(new Point(x, y - y / 2), -Math.PI / 2);
		Triple<Point, Point, Point> middle = checkLine(new Point(x, y), -Math.PI / 2);
		Triple<Point, Point, Point> bottom = checkLine(new Point(x, y + y / 2), -Math.PI / 2);
		Triple<Point, Point, Point> abottom = checkLine(new Point(x, 2 * y - 10), -Math.PI / 2);

		Triple<Point, Point, Point> primary = bottom;
		Triple<Point, Point, Point> secondary = abottom;

		if (primary == null) {
			primary = middle;
			secondary = null;
		}
		if (primary == null) {
			primary = top;
		}
		if (primary == null) {
			primary = abottom;
		}
		if (observeTopMillis + 1000 > System.currentTimeMillis()) {
			System.out.println("Skipping to top");
			primary = top;
			//return new RobotInstruction(d.image, d, new Point(0, 1));
		}
		if (primary == null) {
			return new RobotInstruction(d.image, d, new Point(0, 1));
		}

		Point destination;
		if (secondary == null) {
			destination = new Point(primary.getMiddle());
		} else {
			destination = new Point((int) Math.round(1.5 * bottom.getMiddle().x - 0.5 * abottom.getMiddle().x), bottom.getMiddle().y);
		}
		
		Point intersection = mIntersectionSolver.findMark(d.image, primary.getMiddle().y);
		if (intersection != null) {
			System.out.println("Found green mark");
			d.highlight.add(intersection);
			//lastInstruction = new RobotInstruction(d.image, d, new Point((diff > 0 ? 1 : -1) * (d.image.getWidth() / 2), primary.getMiddle().y));
			lastInstruction = new RobotInstruction(d.image, d, new Point(40, primary.getMiddle().y));
			lastInstructionUntil = System.currentTimeMillis() + 600;
			return lastInstruction;
		}

		if (primary == bottom && top != null && d.dst(bottom.getLeft(), bottom.getRight()) > d.image.getWidth() / 3) {
			primary = top;
			observeTopMillis = System.currentTimeMillis();
			System.out.println("Recognized intersection");
		}
		
		if (secondary == null) {
			destination = new Point(primary.getMiddle());
		} else {
			destination = new Point((int) Math.round(1.5 * bottom.getMiddle().x - 0.5 * abottom.getMiddle().x), bottom.getMiddle().y);
		}
		
		destination.translate(-x, 0);

		return new RobotInstruction(d.image, d, destination);
	}

	public RobotInstruction evaluateB() {
		// process image
		if (mImageModifier != null)
			d.image = mImageModifier.modify(d.image);

		// create boolean array
		d.bw = mBooleanImageFilter.filter(d.image);

		Point center = new Point(d.image.getWidth() / 2, d.image.getHeight() * 3 / 4);
		int radius = d.image.getHeight() / 2;
		List<Interval> intervals = checkCircle(center, radius);

		if (intervals.size() == 0) {
			return new RobotInstruction(d.image, d, new Point(0, 1));
		}

		double leastDifference = Double.MAX_VALUE;
		Interval leastDifferenceInterval = null;
		for (Interval i : intervals) {
			if (Math.abs(i.getAverage()) < leastDifference) {
				leastDifference = Math.abs(i.getAverage());
				leastDifferenceInterval = i;
			}
			d.highlight.add(d.move(center, i.getAverage() - Math.PI / 2, radius));
		}

		int heading = (int) (leastDifferenceInterval.getAverage() * 100);

		return new RobotInstruction(d.image, d, new Point(heading, 0));
	}

	/**
	 * Checks trail on the given straight line, which is specified by a point
	 * and a direction.
	 * 
	 * @param p
	 * @param direction
	 * @return
	 */
	public Triple<Point, Point, Point> checkLine(Point p, double direction) {
		Point trail = d.findLinearlyNearestPoint(p, direction + Math.PI / 2, true, Config.movedst, Integer.MAX_VALUE);

		if (trail != null) {

			ImmutablePair<Point, Point> borders = mBorderFinder.findBorders(d, trail, direction);

			if (borders != null) {
				d.bordersL.add(borders.left);
				d.bordersR.add(borders.right);

				// Point l = d.avg(borders.getLeft(), borders.getRight());
				Point center = new Point(d.image.getWidth() / 2, p.y);
				double dstL = d.dst(center, borders.left);
				double dstR = d.dst(center, borders.right);
				Point l;
				if (Math.abs(dstL - dstR) < 5) {
					l = center;
				} else {
					Point moreDistanced = (dstL > dstR) ? borders.left : borders.right;
					Point averaged = d.weightedAvg(moreDistanced, center, d.dst(center, moreDistanced) / center.x);
					l = new Point(MathTools.toRange(averaged.x, borders.left.x, borders.right.x),
							MathTools.toRange(averaged.y, Math.min(borders.left.y, borders.right.y),
									Math.max(borders.left.y, borders.right.y)));
				}
				d.line.add(l);

				return new ImmutableTriple<Point, Point, Point>(borders.left, l, borders.right);
			}
		}

		return null;
	}

	public List<Interval> checkCircle(Point center, int radius) {
		List<Interval> list = new ArrayList<Interval>();
		boolean last = true, val, first = true;
		double beginAngle = 0, lastAngle = 0;
		for (double angle = -Math.PI; angle <= Math.PI; angle += Math.PI * 2 / 40) {
			Point p = d.move(center, angle - Math.PI / 2, radius);
			if (!d.inBounds(p)) {
				continue;
			}
			d.line.add(p);
			val = d.isTrailPoint(p);
			if (last == false && val == true) {
				beginAngle = angle;
				first = false;
				last = true;
			}
			if (last == true && val == false) {
				last = false;
				if (first) {
					first = false;
					continue;
				}
				list.add(new Interval(beginAngle, lastAngle));
			}
			lastAngle = angle;
		}
		return list;
	}

	/**
	 * Image output whose purpose is drawing image from {@link RobotInstruction}
	 * that originates in the {@link HorizontalLevelObservingStrategy}.
	 * 
	 * @author JJurM
	 */
	public static class ImageOutput extends net.talentum.jackie.image.output.ImageOutput {

		HorizontalLevelObservingStrategy strategy;

		public ImageOutput(String name, HorizontalLevelObservingStrategy strategy) {
			super(name);
			this.strategy = strategy;
		}

		@Override
		public BufferedImage process(BufferedImage image) {
			strategy.prepare(image);
			RobotInstruction instruction = strategy.evaluate();
			MomentData d = instruction.momentData;

			BufferedImage img = InstructionPainter.getBooleanImage(d.bw);
			Graphics g = img.getGraphics();

			int y = d.image.getHeight() / 2;
			g.setColor(Color.BLUE);
			g.drawLine(0, y / 2, d.image.getWidth(), y / 2);
			g.drawLine(0, y, d.image.getWidth(), y);
			g.drawLine(0, y * 3 / 2, d.image.getWidth(), y * 3 / 2);
			g.drawLine(0, 2 * y - 10, d.image.getWidth(), 2 * y - 10);

			// g.drawOval((d.image.getWidth() - d.image.getHeight()) / 2, y,
			// image.getHeight(), image.getHeight());

			g.setColor(Color.GREEN);
			d.bordersL.stream().forEach(p -> g.fillOval(p.x - 6, p.y - 6, 12, 12));
			d.bordersR.stream().forEach(p -> g.fillOval(p.x - 6, p.y - 6, 12, 12));

			g.setColor(Color.RED);
			d.line.stream().forEach(p -> g.fillRect(p.x - 2, p.y - 2, 4, 4));

			g.setColor(Color.CYAN);
			d.highlight.stream().forEach(p -> g.fillRect(p.x - 2, p.y - 2, 4, 4));

			g.setColor(Color.YELLOW);

			return img;
		}

	}

}
