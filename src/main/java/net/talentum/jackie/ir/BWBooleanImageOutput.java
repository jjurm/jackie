package net.talentum.jackie.ir;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import net.talentum.jackie.moment.Moment;
import net.talentum.jackie.moment.module.BWBooleanImageFilterModule;

public class BWBooleanImageOutput extends ImageRecognitionOutput {

	BWBooleanImageFilterModule filter;

	public BWBooleanImageOutput(String name, int treshold) {
		super(name);
		this.filter = new BWBooleanImageFilterModule(treshold);
	}

	@Override
	public BufferedImage process(Moment moment) {
		boolean[][] bw = filter.filter(moment.image);

		BufferedImage target = new BufferedImage(moment.image.getWidth(), moment.image.getHeight(),
				BufferedImage.TYPE_INT_RGB);
		Graphics g = target.getGraphics();

		for (int y = 0; y < moment.image.getHeight(); y++) {
			for (int x = 0; x < moment.image.getWidth(); x++) {
				g.setColor(bw[x][y] ? Color.BLACK : Color.WHITE);
				g.fillRect(x, y, 1, 1);
			}
		}

		return target;
	}

}
