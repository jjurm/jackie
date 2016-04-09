package net.talentum.jackie.image;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;

import net.talentum.jackie.image.output.ImageOutput;
import net.talentum.jackie.module.impl.BlurImageModifierModule;
import net.talentum.jackie.robot.Moment;
import net.talentum.jackie.system.ConfigurationManager;

/**
 * Ball finder that finds ball by comparing a photo without added light with
 * photo with supplied light.
 * 
 * @author JJurM
 */
public class SubtractingImageBallFinder extends ImageOutput {

	BlurImageModifierModule blur = new BlurImageModifierModule();
	boolean first = true;

	BufferedImage img1;
	BufferedImage img2;
	BufferedImage result;

	public SubtractingImageBallFinder(String name) {
		super(name);
	}

	@Override
	public BufferedImage process(Moment moment) {
		if (first) {
			first = false;
			img1 = moment.image;
			return img1;
		} else {
			first = true;
			img2 = moment.image;

			Point p = find(img1, img2);

			if (p != null) {
				Graphics g = result.getGraphics();
				g.setColor(Color.RED);
				g.drawLine(p.x, 0, p.x, result.getHeight());
				g.drawLine(0, p.y, result.getWidth(), p.y);
				g.setColor(Color.WHITE);
				g.fillOval(p.x - 2, p.y - 2, 4, 4);
			}

			return result;
		}
	}

	/**
	 * Finds point of ball, or returns {@code null} if the ball was not found.
	 * 
	 * @param img1 image without light
	 * @param img2 image with light
	 * @return
	 */
	public Point find(BufferedImage img1, BufferedImage img2) {
		result = new BufferedImage(img1.getWidth(), img1.getHeight(), BufferedImage.TYPE_INT_RGB);
		Color c1, c2, r;
		for (int x = 0; x < img1.getWidth(); x++) {
			for (int y = 0; y < img1.getHeight(); y++) {
				c1 = new Color(img1.getRGB(x, y));
				c2 = new Color(img2.getRGB(x, y));
				r = new Color(Math.max(c2.getRed() - c1.getRed(), 0), Math.max(c2.getBlue() - c1.getBlue(), 0),
						Math.max(c2.getGreen() - c1.getGreen(), 0));
				result.setRGB(x, y, r.getRGB());
			}
		}

		result = blur.modify(result);

		double val;
		double maxVal = ConfigurationManager.getGeneralConfiguration().getInt("params/ballFinding/minMaxValue");
		Point maxPoint = null;
		for (int x = 0; x < result.getWidth(); x++) {
			for (int y = 0; y < result.getHeight(); y++) {
				r = new Color(result.getRGB(x, y));
				val = ((int) (r.getRed() * 0.299) + (int) (r.getGreen() * 0.587) + (int) (r.getBlue() * 0.114));
				if (val > maxVal) {
					maxVal = val;
					maxPoint = new Point(x, y);
				}
			}
		}
		return maxPoint;
	}

}
