package net.talentum.jackie.moment;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Deque;
import java.util.LinkedList;
import java.util.function.Supplier;

import org.apache.commons.lang3.tuple.ImmutablePair;

import net.talentum.jackie.libs.PIDController;
import net.talentum.jackie.moment.module.AveragingTrailWidthDeterminerModule;
import net.talentum.jackie.moment.module.BasicAngularTurnHandlerModule;
import net.talentum.jackie.moment.module.BasicBorderFinderModule;
import net.talentum.jackie.moment.module.BasicLineFinderModule;
import net.talentum.jackie.moment.module.BlurImageModifierModule;
import net.talentum.jackie.moment.module.BottomLineStartFinderModule;
import net.talentum.jackie.moment.module.MotorIntensityFunction;
import net.talentum.jackie.moment.module.UnivBooleanImageFilterModule;
import net.talentum.jackie.moment.module.VectorDirectionManagerModule;
import net.talentum.jackie.moment.strategy.LineFollowingStrategy;
import net.talentum.jackie.moment.strategy.RobotStrategy;
import net.talentum.jackie.serial.SerialCommunicator;

/**
 * One instance of this class represents one robot (there should naturally be at
 * most one instance). Robot is responsible for constructing {@link Moment}s
 * 
 * @author JJurM
 */
public class Robot {

	Parameters param;

	/**
	 * Holds object that is capable of supplying webcam images.
	 */
	Supplier<BufferedImage> webcamImageSupplier;

	/**
	 * Shallow history of past moments
	 * 
	 * @see #constructMoment()
	 */
	public Deque<Moment> moments = new LinkedList<Moment>();
	public RobotInstruction lastInstruction;
	public SerialCommunicator serial;

	protected RobotStrategy strategy;
	protected PIDController pid;
	protected MotorIntensityFunction mif;

	public Robot(Parameters param) {
		this.param = param;

		// open serial communication
		serial = new SerialCommunicator();

		// create strategy
		// @formatter:off
		strategy = new LineFollowingStrategy(
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
		// @formatter:on

		// create and setup PIDController
		pid = new PIDController(0.5, 0.25, 0.25);
		pid.setInputRange(-Math.PI / 2, Math.PI / 2);
		pid.setOutputRange(-Math.PI / 2, Math.PI / 2);
		pid.setSetpoint(0);
		pid.enable();
	}

	public void setWebcamImageSupplier(Supplier<BufferedImage> webcamImageSupplier) {
		this.webcamImageSupplier = webcamImageSupplier;
	}

	/**
	 * Constructs {@link Moment}, which involves taking an image from webcam
	 * supplier and collecting sensor data.
	 * 
	 * @return
	 */
	public Moment constructMoment() {
		BufferedImage image = webcamImageSupplier.get();
		SensorData sensorData = SensorData.collect();

		Moment moment = new Moment(image, sensorData);

		moments.push(moment);
		while (moments.size() > 2) {
			moments.pollLast();
		}
		return moment;
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

		lastInstruction = instruction;
		return instruction;
	}

	/**
	 * Contains the main cycle that is run by the robot.
	 */
	public void run() {
		while (true) {
			
			System.out.println("Running main robot cycle");

			// obtain moment
			Moment moment = constructMoment();

			// process
			RobotInstruction instruction = process(moment);

			// set motors
			setMotors(instruction.destination);

		}
	}

	/**
	 * Sets motors for given destination. The value is first inserted into the
	 * {@link PIDController} and then evaluated by a
	 * {@link MotorIntensityFunction}. Finally, obtained values are written to
	 * serial.
	 * 
	 * @param destination
	 */
	public void setMotors(Point destination) {

		// check if the result is valid
		if (destination == null || destination.equals(new Point(0, 0))) {
			return;
		}

		// get direction
		double direction = Math.PI / 2 - Math.atan2(destination.y, destination.x);
		pid.getInput(-direction);

		// compute heading (= control variable of PID controller)
		double heading = pid.performPID();

		// get angle values to send
		ImmutablePair<Integer, Integer> motors = mif.getMotors(heading);

		// write to serial
		serial.write(1, motors.left, motors.right);

	}

}
