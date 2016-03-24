package net.talentum.jackie.moment.module;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.function.Function;

public class UnivBooleanImageFilterModule implements BooleanImageFilterModule {

	protected Function<Color, Boolean> function;

	public UnivBooleanImageFilterModule(final int treshold) {
		this.function = new Function<Color, Boolean>() {
			@Override
			public Boolean apply(Color c) {
				return ((int) (c.getRed() * 0.299) + (int) (c.getGreen() * 0.587) + (int) (c.getBlue() * 0.114)) > treshold;
			}
		};
	}
	
	public UnivBooleanImageFilterModule(Function<Color, Boolean> function) {
		this.function = function;
	}

	@Override
	public boolean[][] filter(BufferedImage img) {
		boolean[][] bool = new boolean[img.getWidth()][img.getHeight()];

		for (int i = 0; i < img.getHeight(); i++) {
			for (int j = 0; j < img.getWidth(); j++) {
				Color c = new Color(img.getRGB(j, i));
				bool[j][i] = !function.apply(c);
			}
		}

		return bool;
	}
	
}
