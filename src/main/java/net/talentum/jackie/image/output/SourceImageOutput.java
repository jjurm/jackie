package net.talentum.jackie.image.output;

import java.awt.image.BufferedImage;

import net.talentum.jackie.robot.Moment;

public class SourceImageOutput extends ImageOutput {

	public SourceImageOutput(String name) {
		super(name);
	}

	@Override
	public BufferedImage process(Moment moment) {
		return moment.image;
	}
	
}
