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

public class Robot {

	Parameters param;

	Supplier<BufferedImage> webcamImageSupplier;

	public Deque<Moment> moments = new LinkedList<Moment>();
	public RobotInstruction lastInstruction;
	public SerialCommunicator serial;

	protected RobotStrategy strategy;
	protected PIDController pid;
	protected MotorIntensityFunction mif;

	public Robot(Parameters param) {
		this.param = param;

		serial = new SerialCommunicator();
		serial.open();

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

		pid = new PIDController(0.5, 0.25, 0.25);
		pid.setInputRange(-Math.PI / 2, Math.PI / 2);
		pid.setOutputRange(-Math.PI / 2, Math.PI / 2);
		pid.setSetpoint(0);
		pid.enable();
	}

	public void setWebcamImageSupplier(Supplier<BufferedImage> webcamImageSupplier) {
		this.webcamImageSupplier = webcamImageSupplier;
	}

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

	public final synchronized RobotInstruction process(Moment moment) {
		strategy.prepare(moment);
		RobotInstruction instruction = strategy.evaluate();

		lastInstruction = instruction;
		return instruction;
	}

	public void run() {
		while (true) {

			// obtain moment
			Moment moment = constructMoment();

			// process
			RobotInstruction instruction = process(moment);

			// set motors
			setMotors(instruction.destination);

		}
	}

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
