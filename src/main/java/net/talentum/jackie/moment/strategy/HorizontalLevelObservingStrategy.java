package net.talentum.jackie.moment.strategy;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;

import org.apache.commons.lang3.tuple.ImmutablePair;

import net.talentum.jackie.moment.Moment;
import net.talentum.jackie.moment.MomentData;
import net.talentum.jackie.moment.Parameters;
import net.talentum.jackie.moment.RobotInstruction;
import net.talentum.jackie.moment.module.BooleanImageFilterModule;
import net.talentum.jackie.moment.module.BorderFinderModule;
import net.talentum.jackie.moment.module.ImageModifierModule;
import net.talentum.jackie.tools.InstructionPainter;

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

	public HorizontalLevelObservingStrategy(Parameters param, ImageModifierModule mImageModifier,
			BooleanImageFilterModule mBooleanImageFilter, BorderFinderModule mBorderFinder) {
		super(param);
		this.mImageModifier = mImageModifier;
		this.mBooleanImageFilter = mBooleanImageFilter;
		this.mBorderFinder = mBorderFinder;
	}

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
		Point abottom = checkLine(new Point(x, 2*y-1), -Math.PI / 2);

		Point destination = bottom;
		
		if (destination != null) {
			destination = new Point(destination);
			destination.translate(-x, 0);
		}

		RobotInstruction instruction = new RobotInstruction(d.m, d, destination);
		return instruction;
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
		Point trail = d.findLinearlyNearestPoint(p, direction + Math.PI / 2, true, param.movedst, Integer.MAX_VALUE);

		if (trail != null) {

			ImmutablePair<Point, Point> borders = mBorderFinder.findBorders(d, trail, direction);

			if (borders != null) {
				d.bordersL.add(borders.left);
				d.bordersR.add(borders.right);

				Point l = d.avg(borders.getLeft(), borders.getRight());
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
	public static class ImageOutput extends net.talentum.jackie.ir.ImageOutput {

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
			g.drawLine(0, 2*y-1, d.image.getWidth(), 2*y-1);

			g.setColor(Color.RED);
			d.line.stream().forEach(p -> g.fillRect(p.x - 2, p.y - 2, 4, 4));

			g.setColor(Color.GREEN);
			d.bordersL.stream().forEach(p -> g.fillOval(p.x - 6, p.y - 6, 12, 12));
			d.bordersR.stream().forEach(p -> g.fillOval(p.x - 6, p.y - 6, 12, 12));

			return img;
		}

	}

}
