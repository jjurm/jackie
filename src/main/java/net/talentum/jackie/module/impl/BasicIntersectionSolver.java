package net.talentum.jackie.module.impl;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.function.Function;

import net.talentum.jackie.module.IntersectionSolver;
import net.talentum.jackie.system.Config;

public class BasicIntersectionSolver implements IntersectionSolver {

	Function<Color, Double> function;

	public BasicIntersectionSolver() {
		function = new Function<Color, Double>() {
			@Override
			public Double apply(Color c) {
				return ((double) c.getGreen()) * 100 / (c.getBlue() + c.getRed() + 1);
			}
		};
	}

	/**
	 * Finds intersection mark or returns {@code null}.
	 */
	@Override
	public Point findMark(BufferedImage img, int y) {
		Color c;
		double val;

		double maxVal = 0;
		Point maxPoint = null;
		for (int x = 0; x < img.getWidth(); x++) {
			c = new Color(img.getRGB(x, y));
			val = function.apply(c);
			if (val > maxVal) {
				maxVal = val;
				maxPoint = new Point(x, y);
			}
		}
		if (maxVal >= Config.get().getDouble("params/intersections/minMaxValue")) {
			return maxPoint;
		} else {
			return null;
		}
	}

}
