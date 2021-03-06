package net.talentum.jackie.image.output;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.function.Function;

import net.talentum.jackie.module.impl.BlurImageModifierModule;
import net.talentum.jackie.module.impl.UnivBooleanImageFilterModule;
import net.talentum.jackie.tools.InstructionPainter;

public class BlurredBooleanImageOutput extends ImageOutput {

	private BlurImageModifierModule blurModifier;
	private UnivBooleanImageFilterModule bwFilter;

	public BlurredBooleanImageOutput(String name, int bwTreshold) {
		super(name);
		blurModifier = new BlurImageModifierModule();
		this.bwFilter = new UnivBooleanImageFilterModule(bwTreshold);
	}
	
	public BlurredBooleanImageOutput(String name, Function<Color, Boolean> function) {
		super(name);
		blurModifier = new BlurImageModifierModule();
		this.bwFilter = new UnivBooleanImageFilterModule(function);
	}

	@Override
	public BufferedImage process(BufferedImage image) {
		BufferedImage blurred = blurModifier.modify(image);
		boolean[][] bw = bwFilter.filter(blurred);
		return InstructionPainter.getBooleanImage(bw);
	}

}
