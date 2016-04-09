package net.talentum.jackie.image.output;

import java.awt.image.BufferedImage;

import net.talentum.jackie.robot.Moment;
import net.talentum.jackie.robot.RobotInstruction;
import net.talentum.jackie.robot.RobotInstructionRegister;
import net.talentum.jackie.robot.strategy.RobotStrategy;
import net.talentum.jackie.tools.InstructionPainter;

public class RobotStrategyIROutput extends ImageOutput {

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
