package net.talentum.jackie.ir;

import java.awt.image.BufferedImage;

import net.talentum.jackie.moment.Moment;
import net.talentum.jackie.moment.module.BWBooleanImageFilterModule;

public class BWBooleanImageOutput extends ImageRecognitionOutput {

	private BWBooleanImageFilterModule filter;

	public BWBooleanImageOutput(String name, int treshold) {
		super(name);
		this.filter = new BWBooleanImageFilterModule(treshold);
	}

	@Override
	public BufferedImage process(Moment moment) {
		boolean[][] bw = filter.filter(moment.image);
		return filter.getImage(bw);
	}

}
