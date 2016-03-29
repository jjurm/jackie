package net.talentum.jackie.state;

import java.awt.Point;

import net.talentum.jackie.moment.Moment;
import net.talentum.jackie.moment.Parameters;
import net.talentum.jackie.moment.Robot;
import net.talentum.jackie.moment.RobotInstruction;
import net.talentum.jackie.moment.module.AveragingTrailWidthDeterminerModule;
import net.talentum.jackie.moment.module.BasicAngularTurnHandlerModule;
import net.talentum.jackie.moment.module.BasicBorderFinderModule;
import net.talentum.jackie.moment.module.BasicLineFinderModule;
import net.talentum.jackie.moment.module.BlurImageModifierModule;
import net.talentum.jackie.moment.module.BottomLineStartFinderModule;
import net.talentum.jackie.moment.module.LinearMotorIntensityFunction;
import net.talentum.jackie.moment.module.MotorIntensityFunction;
import net.talentum.jackie.moment.module.UnivBooleanImageFilterModule;
import net.talentum.jackie.moment.module.VectorDirectionManagerModule;
import net.talentum.jackie.moment.strategy.LineFollowingStrategy;
import net.talentum.jackie.moment.strategy.RobotStrategy;

import org.apache.commons.lang3.tuple.ImmutablePair;

public class LineFollowingState implements State {

	private RobotStrategy strategy;
	private Robot robot;
	private MotorIntensityFunction mif;
	
	public LineFollowingState(Parameters param, Robot robot) {
		this.robot = robot;
		this.mif = new LinearMotorIntensityFunction();
		
		this.strategy = new LineFollowingStrategy(
				param,
				new BlurImageModifierModule(),
				new UnivBooleanImageFilterModule(100),
				new BottomLineStartFinderModule(),
				(d) -> new AveragingTrailWidthDeterminerModule(d, 3),
				(d) -> new VectorDirectionManagerModule(8, 3),
				new BasicLineFinderModule(
						20.0 * (Math.PI / 180),
						new BasicBorderFinderModule(2, 140, 10),
						new BasicAngularTurnHandlerModule()
				)
		);
	}
	
	/**
	 * Lets the strategy process the moment. Returns {@link RobotInstruction}.
	 * 
	 * @param moment
	 * @return
	 */
	public final synchronized RobotInstruction process(Moment moment) {
		strategy.prepare(moment);
		RobotInstruction instruction = strategy.evaluate();

		robot.lastInstruction = instruction;
		return instruction;
	}
	
	@Override
	public ImmutablePair<Integer, Integer> getMotorInstructions() {
		// obtain moment
		Moment moment = robot.constructMoment();

		// process
		RobotInstruction instruction = process(moment);

		double direction = 0.0;
		// set motors
		// check if the result is valid
		if (instruction.destination != null && !instruction.destination.equals(new Point(0, 0))) {
			// get direction
			direction = Math.PI / 2 - Math.atan2(instruction.destination.y, instruction.destination.x);
		}
		
		// get angle values to send
		ImmutablePair<Integer, Integer> motors = mif.getMotors(direction);

		return motors;
		
	}

}
