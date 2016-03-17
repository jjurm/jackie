package net.talentum.jackie.ir;

import java.awt.image.BufferedImage;

import net.talentum.jackie.moment.Moment;
import net.talentum.jackie.moment.RobotInstruction;
import net.talentum.jackie.moment.strategy.RobotStrategy;
import net.talentum.jackie.tools.InstructionPainter;

public class RobotStrategyIROutput extends ImageRecognitionOutput {

	RobotStrategy strategy;

	public RobotStrategyIROutput(String name, RobotStrategy strategy) {
		super(name);
		this.strategy = strategy;
	}

	@Override
	public BufferedImage process(Moment moment) {
		strategy.prepare(moment);
		RobotInstruction instruction = strategy.evaluate();

		BufferedImage image = InstructionPainter.paintOnImage(moment.image, instruction);
		
		return image;
	}

}
