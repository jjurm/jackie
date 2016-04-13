package net.talentum.jackie.image.output;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.function.Function;

import net.talentum.jackie.module.impl.UnivBooleanImageFilterModule;
import net.talentum.jackie.tools.InstructionPainter;

public class BooleanImageOutput extends ImageOutput {

	private UnivBooleanImageFilterModule filter;

	public BooleanImageOutput(String name, int treshold) {
		super(name);
		this.filter = new UnivBooleanImageFilterModule(treshold);
	}
	
	public BooleanImageOutput(String name, Function<Color, Boolean> function) {
		super(name);
		this.filter = new UnivBooleanImageFilterModule(function);
	}

	@Override
	public BufferedImage process(BufferedImage image) {
		boolean[][] bw = filter.filter(image);
		return InstructionPainter.getBooleanImage(bw);
	}

}
