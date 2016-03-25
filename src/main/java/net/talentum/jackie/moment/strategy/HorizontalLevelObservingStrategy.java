package net.talentum.jackie.moment.strategy;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;

import org.apache.commons.lang3.tuple.ImmutablePair;

import net.talentum.jackie.ir.ImageRecognitionOutput;
import net.talentum.jackie.moment.Moment;
import net.talentum.jackie.moment.MomentData;
import net.talentum.jackie.moment.Parameters;
import net.talentum.jackie.moment.RobotInstruction;
import net.talentum.jackie.moment.module.BooleanImageFilterModule;
import net.talentum.jackie.moment.module.BorderFinderModule;
import net.talentum.jackie.moment.module.ImageModifierModule;
import net.talentum.jackie.tools.InstructionPainter;

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

		checkLine(new Point(x, y - y / 2), -Math.PI / 2);
		Point destination = new Point(checkLine(new Point(x, y), -Math.PI / 2));
		checkLine(new Point(x, y + y / 2), -Math.PI / 2);

		destination.translate(-x, 0);

		RobotInstruction instruction = new RobotInstruction(d.m, d, destination);
		return instruction;
	}

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

	public static class ImageOutput extends ImageRecognitionOutput {

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

			g.setColor(Color.RED);
			d.line.stream().forEach(p -> g.fillRect(p.x - 2, p.y - 2, 4, 4));

			g.setColor(Color.GREEN);
			d.bordersL.stream().forEach(p -> g.fillOval(p.x - 6, p.y - 6, 12, 12));
			d.bordersR.stream().forEach(p -> g.fillOval(p.x - 6, p.y - 6, 12, 12));

			return img;
		}

	}

}
