package net.talentum.jackie.ir;

import java.awt.image.BufferedImage;

import net.talentum.jackie.moment.Moment;
import net.talentum.jackie.moment.module.BWBooleanImageFilterModule;
import net.talentum.jackie.moment.module.BlurImageModifierModule;

public class BlurredBWBooleanImageOutput extends ImageRecognitionOutput {

	private BlurImageModifierModule blurModifier;
	private BWBooleanImageFilterModule bwFilter;

	public BlurredBWBooleanImageOutput(String name, int bwTreshold) {
		super(name);
		blurModifier = new BlurImageModifierModule();
		this.bwFilter = new BWBooleanImageFilterModule(bwTreshold);
	}

	@Override
	public BufferedImage process(Moment moment) {
		BufferedImage blurred = blurModifier.modify(moment.image);
		boolean[][] bw = bwFilter.filter(blurred);
		return bwFilter.getImage(bw);
	}

}
