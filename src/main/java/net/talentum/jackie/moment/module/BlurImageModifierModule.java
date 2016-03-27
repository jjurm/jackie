package net.talentum.jackie.moment.module;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

import net.talentum.jackie.ir.ImageOutput;
import net.talentum.jackie.moment.Moment;

public class BlurImageModifierModule extends ImageOutput implements ImageModifierModule {

	public BlurImageModifierModule(String name) {
		super(name);
	}
	
	public BlurImageModifierModule() {
		this("Blur");
	}

	private static final float[] matrix = new float[] { 1.0f / 121.0f, 2.0f / 121.0f, 3.0f / 121.0f, 2.0f / 121.0f, 1.0f / 121.0f,
			2.0f / 121.0f, 7.0f / 121.0f, 11.0f / 121.0f, 7.0f / 121.0f, 2.0f / 121.0f, 3.0f / 121.0f,
			11.0f / 121.0f, 17.0f / 121.0f, 11.0f / 121.0f, 3.0f / 121.0f, 2.0f / 121.0f, 7.0f / 121.0f,
			11.0f / 121.0f, 7.0f / 121.0f, 2.0f / 121.0f, 1.0f / 121.0f, 2.0f / 121.0f, 3.0f / 121.0f,
			2.0f / 121.0f, 1.0f / 121.0f };
	
	@Override
	public BufferedImage modify(BufferedImage image) {
		
		BufferedImage src = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics g = src.getGraphics();
		g.drawImage(image, 0, 0, null);
		g.dispose();

		BufferedImage dest = new BufferedImage(image.getWidth() - 10, image.getHeight() - 10, BufferedImage.TYPE_INT_RGB);
		BufferedImageOp op = new ConvolveOp(new Kernel(5, 5, matrix));

		// image = op.filter(src, dest);
		op.filter(src, dest);

		return dest;
		
	}

	@Override
	public BufferedImage process(Moment moment) {
		return modify(moment.image);
	}

}
