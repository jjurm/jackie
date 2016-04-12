package net.talentum.jackie.robot.strategy;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;

import org.apache.commons.lang3.tuple.ImmutablePair;

import net.talentum.jackie.module.BooleanImageFilterModule;
import net.talentum.jackie.module.BorderFinderModule;
import net.talentum.jackie.module.ImageModifierModule;
import net.talentum.jackie.module.IntersectionSolver;
import net.talentum.jackie.robot.Moment;
import net.talentum.jackie.robot.MomentData;
import net.talentum.jackie.robot.RobotInstruction;
import net.talentum.jackie.system.Config;
import net.talentum.jackie.tools.InstructionPainter;
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

	public HorizontalLevelObservingStrategy(ImageModifierModule mImageModifier,
			BooleanImageFilterModule mBooleanImageFilter, BorderFinderModule mBorderFinder,
			IntersectionSolver mIntersectionSolver) {
		this.mImageModifier = mImageModifier;
		this.mBooleanImageFilter = mBooleanImageFilter;
		this.mBorderFinder = mBorderFinder;
		this.mIntersectionSolver = mIntersectionSolver;
	}

	@SuppressWarnings("unused")
	@Override
	public RobotInstruction evaluate() {
		// process image
		if (mImageModifier != null)
			d.image = mImageModifier.modify(d.image);

		// create boolean array
		d.bw = mBooleanImageFilter.filter(d.image);

		// find line
		int x = d.image.getWidth() / 2;
		int y = d.image.getHeight() / 2;

		Point top = checkLine(new Point(x, y - y / 2), -Math.PI / 2);
		Point middle = checkLine(new Point(x, y), -Math.PI / 2);
		Point bottom = checkLine(new Point(x, y + y / 2), -Math.PI / 2);
		Point abottom = checkLine(new Point(x, 2 * y - 10), -Math.PI / 2);

		Point primary = bottom;
		Point secondary = null;

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
		if (primary == null) {
			primary = abottom;
		}
		if (primary == null) {
			return new RobotInstruction(d.m, d, new Point(0, 1));
		}

		Point destination;
		if (secondary == null) {
			destination = new Point(bottom);
		} else {
			destination = new Point((int) Math.round(1.5 * bottom.x - 0.5 * abottom.x), bottom.y);
		}

		Point intersection = mIntersectionSolver.findMark(d.image, primary.y);
		if (intersection != null) {
			d.highlight.add(intersection);

			int diff = intersection.x - destination.x;
			destination.x += diff * Config.get().getDouble("params/lineFollowing/intersectionFactor");
		}

		destination.translate(-x, 0);

		return new RobotInstruction(d.m, d, destination);
	}

	/**
	 * Checks trail on the given straight line, which is specified by a point
	 * and a direction.
	 * 
	 * @param p
	 * @param direction
	 * @return
	 */
	public Point checkLine(Point p, double direction) {
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
				Point moreDistanced = (dstL > dstR) ? borders.left : borders.right;
				Point averaged = d.weightedAvg(moreDistanced, center, d.dst(center, moreDistanced) / center.x);
				Point l = new Point(MathTools.toRange(averaged.x, borders.left.x, borders.right.x),
						MathTools.toRange(averaged.y, Math.min(borders.left.y, borders.right.y),
								Math.max(borders.left.y, borders.right.y)));
				d.line.add(l);

				return l;
			}
		}

		return null;
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
		public BufferedImage process(Moment moment) {
			strategy.prepare(moment);
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

			g.setColor(Color.GREEN);
			d.bordersL.stream().forEach(p -> g.fillOval(p.x - 6, p.y - 6, 12, 12));
			d.bordersR.stream().forEach(p -> g.fillOval(p.x - 6, p.y - 6, 12, 12));

			g.setColor(Color.RED);
			d.line.stream().forEach(p -> g.fillRect(p.x - 2, p.y - 2, 4, 4));

			g.setColor(Color.CYAN);
			d.highlight.stream().forEach(p -> g.fillRect(p.x - 2, p.y - 2, 4, 4));

			return img;
		}

	}

}
