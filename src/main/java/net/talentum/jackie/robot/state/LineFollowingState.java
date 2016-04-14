package net.talentum.jackie.robot.state;

import java.awt.Point;
import java.awt.image.BufferedImage;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.lang3.tuple.ImmutablePair;

import net.talentum.jackie.libs.PIDController;
import net.talentum.jackie.module.MotorIntensityFunction;
import net.talentum.jackie.module.impl.BasicBorderFinderModule;
import net.talentum.jackie.module.impl.BasicIntersectionSolver;
import net.talentum.jackie.module.impl.BlurImageModifierModule;
import net.talentum.jackie.module.impl.SimpleMotorIntensityFunction;
import net.talentum.jackie.module.impl.UnivBooleanImageFilterModule;
import net.talentum.jackie.robot.Robot;
import net.talentum.jackie.robot.RobotInstruction;
import net.talentum.jackie.robot.strategy.HorizontalLevelObservingStrategy;
import net.talentum.jackie.system.Config;
import net.talentum.jackie.system.ConfigurationManager;

/**
 * State intended for following black line, using the line
 * {@link HorizontalLevelObservingState}.
 * 
 * @author padr31
 *
 */
public class LineFollowingState extends AbstractState {

	//private RobotStrategy strategy;
	public HorizontalLevelObservingStrategy strategy;
	protected PIDController pid;
	private MotorIntensityFunction mif;
	
	private int nearMeasurements = 0;

	public LineFollowingState(Robot robot) {
		super(robot);
		HierarchicalConfiguration config = ConfigurationManager.getGeneralConfiguration();

		this.mif = new SimpleMotorIntensityFunction();

		// @formatter:off
		this.strategy = new HorizontalLevelObservingStrategy(
				new BlurImageModifierModule(),
				new UnivBooleanImageFilterModule(() -> Config.get().getInt("params/bwTreshold")),
				new BasicBorderFinderModule(2, 600, 3),
				new BasicIntersectionSolver()
		);

		// create and setup PIDController
		pid = new PIDController(
				config.getDouble("params/lineFollowing/pid/P"),
				config.getDouble("params/lineFollowing/pid/I"),
				config.getDouble("params/lineFollowing/pid/D")
		);
		pid.setInputRange(-Math.PI / 2, Math.PI / 2);
		pid.setOutputRange(-Math.PI / 2, Math.PI / 2);
		pid.setSetpoint(0);
		pid.enable();
		
		robot.addConfigChangedListener(() -> {
			pid.setPID(
					config.getDouble("params/lineFollowing/pid/P"),
					config.getDouble("params/lineFollowing/pid/I"),
					config.getDouble("params/lineFollowing/pid/D")
			);
		});
		// @formatter:on
	}

	/**
	 * Lets the strategy process the moment. Returns {@link RobotInstruction}.
	 * 
	 * @param moment
	 * @return
	 */
	public final synchronized RobotInstruction process(BufferedImage image) {
		strategy.prepare(image);
		RobotInstruction instruction = strategy.evaluate();

		return instruction;
	}

	@Override
	public State run0() {
		double heading = 0.0;

		if (robot.commander.readUltrasonicSensor(0) < 6) {
			nearMeasurements++;
			if (nearMeasurements >= 3) {
				return new ObstacleAvoidanceState(robot);
			}
		} else {
			nearMeasurements = 0;
		}

		// obtain image
		BufferedImage img = robot.getImage();

		if (img != null) {
			// process
			RobotInstruction instruction = process(img);

			// set motors
			// check if the result is valid
			if (instruction.destination != null && !instruction.destination.equals(new Point(0, 0))) {
				// get direction
				// direction = Math.PI / 2 -
				// Math.atan2(instruction.destination.y,
				// instruction.destination.x);
				heading = ((double) instruction.destination.x) / instruction.image.getWidth();
			}
		}

		// compute heading (= control variable of PID controller)
		pid.getInput(-heading);
		heading = pid.performPID();

		// get angle values to send
		ImmutablePair<Integer, Integer> motors = mif.getMotors(heading);

		// finally write motors
		robot.commander.writePropulsionMotors(motors.left, motors.right);

		return this;
	}

}
