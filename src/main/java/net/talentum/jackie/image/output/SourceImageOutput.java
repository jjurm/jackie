package net.talentum.jackie.image.output;

import java.awt.image.BufferedImage;

public class SourceImageOutput extends ImageOutput {

	public SourceImageOutput(String name) {
		super(name);
	}

	@Override
	public BufferedImage process(BufferedImage image) {
		return image;
	}

}
