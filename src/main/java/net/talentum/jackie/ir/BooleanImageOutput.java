package net.talentum.jackie.ir;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.function.Function;

import net.talentum.jackie.moment.Moment;
import net.talentum.jackie.moment.module.UnivBooleanImageFilterModule;
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
	public BufferedImage process(Moment moment) {
		boolean[][] bw = filter.filter(moment.image);
		return InstructionPainter.getBooleanImage(bw);
	}

}
