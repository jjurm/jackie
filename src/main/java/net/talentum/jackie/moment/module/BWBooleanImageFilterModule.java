package net.talentum.jackie.moment.module;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class BWBooleanImageFilterModule implements BooleanImageFilterModule {

	protected int treshold;

	public BWBooleanImageFilterModule(int treshold) {
		this.treshold = treshold;
	}

	@Override
	public boolean[][] filter(BufferedImage img) {
		boolean[][] bool = new boolean[img.getWidth()][img.getHeight()];
		int val;

		for (int i = 0; i < img.getHeight(); i++) {
			for (int j = 0; j < img.getWidth(); j++) {
				Color c = new Color(img.getRGB(j, i));
				val = (short) ((int) (c.getRed() * 0.299) + (int) (c.getGreen() * 0.587) + (int) (c.getBlue() * 0.114));
				if (val <= treshold) {
					bool[j][i] = true;
				}
			}
		}

		return bool;
	}

}
