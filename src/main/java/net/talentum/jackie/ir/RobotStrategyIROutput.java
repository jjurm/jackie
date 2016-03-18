package net.talentum.jackie.ir;

import java.awt.image.BufferedImage;

import net.talentum.jackie.moment.Moment;
import net.talentum.jackie.moment.RobotInstruction;
import net.talentum.jackie.moment.RobotInstructionRegister;
import net.talentum.jackie.moment.strategy.RobotStrategy;
import net.talentum.jackie.tools.InstructionPainter;

public class RobotStrategyIROutput extends ImageRecognitionOutput {

	RobotStrategy strategy;
	RobotInstructionRegister register;

	public RobotStrategyIROutput(String name, RobotStrategy strategy, RobotInstructionRegister register) {
		super(name);
		this.strategy = strategy;
		this.register = register;
	}

	public RobotStrategyIROutput(String name, RobotStrategy strategy) {
		this(name, strategy, new RobotInstructionRegister(1));
	}

	@Override
	public BufferedImage process(Moment moment) {
		strategy.prepare(moment);
		RobotInstruction instruction = strategy.evaluate();
		register.push(instruction);

		BufferedImage image = InstructionPainter.paintOnImage(moment.image, instruction);

		return image;
	}

}
