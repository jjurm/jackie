package net.talentum.jackie.ir;

import java.awt.image.BufferedImage;

import net.talentum.jackie.moment.Moment;

public class SourceImageOutput extends ImageRecognitionOutput {

	public SourceImageOutput(String name) {
		super(name);
	}

	@Override
	public BufferedImage process(Moment moment) {
		return moment.image;
	}
	
}
