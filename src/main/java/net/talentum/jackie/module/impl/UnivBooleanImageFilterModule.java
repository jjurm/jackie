package net.talentum.jackie.module.impl;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.function.Function;
import java.util.function.Supplier;

import net.talentum.jackie.module.BooleanImageFilterModule;

public class UnivBooleanImageFilterModule implements BooleanImageFilterModule {

	protected Function<Color, Boolean> function;
	protected Supplier<Integer> tresholdSupplier = null;
	protected int suppliedTreshold;

	public UnivBooleanImageFilterModule(final int treshold) {
		this.function = new Function<Color, Boolean>() {
			@Override
			public Boolean apply(Color c) {
				return ((int) (c.getRed() * 0.299) + (int) (c.getGreen() * 0.587)
						+ (int) (c.getBlue() * 0.114)) > treshold;
			}
		};
	}

	public UnivBooleanImageFilterModule(Supplier<Integer> tresholdSupplier) {
		this.tresholdSupplier = tresholdSupplier;
		this.function = new Function<Color, Boolean>() {
			@Override
			public Boolean apply(Color c) {
				return ((int) (c.getRed() * 0.299) + (int) (c.getGreen() * 0.587)
						+ (int) (c.getBlue() * 0.114)) > suppliedTreshold;
			}
		};
	}

	public UnivBooleanImageFilterModule(Function<Color, Boolean> function) {
		this.function = function;
	}

	@Override
	public boolean[][] filter(BufferedImage img) {
		boolean[][] bool = new boolean[img.getWidth()][img.getHeight()];

		if (tresholdSupplier != null) {
			suppliedTreshold = tresholdSupplier.get();
		}

		for (int i = 0; i < img.getHeight(); i++) {
			for (int j = 0; j < img.getWidth(); j++) {
				Color c = new Color(img.getRGB(j, i));
				bool[j][i] = !function.apply(c);
			}
		}

		return bool;
	}

}
