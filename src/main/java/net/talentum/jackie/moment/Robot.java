package net.talentum.jackie.moment;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Deque;
import java.util.LinkedList;
import java.util.function.Supplier;

import org.apache.commons.lang3.tuple.ImmutablePair;

import net.talentum.jackie.libs.PIDController;
import net.talentum.jackie.moment.module.MotorIntensityFunction;
import net.talentum.jackie.moment.strategy.RobotStrategy;
import net.talentum.jackie.serial.SerialCommunicator;
import net.talentum.jackie.state.LineFollowingState;
import net.talentum.jackie.state.State;

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

	private State state;

	public Robot(Parameters param) {
		this.param = param;

		// open serial communication
		serial = new SerialCommunicator();

		// create strategy
		state = new LineFollowingState(param, this);
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
		if (image == null) {
			System.out.println("Got null image");
			return null;
		}
		SensorData sensorData = SensorData.collect();

		Moment moment = new Moment(image, sensorData);

		moments.push(moment);
		while (moments.size() > 2) {
			moments.pollLast();
		}
		return moment;
	}

	/**
	 * Contains the main cycle that is run by the robot.
	 */
	public void run() {
		while (true) {

			System.out.println("Running main robot cycle");

			ImmutablePair<Integer, Integer> motors = state.getMotorInstructions();

			// write to serial
			System.out.println(String.format("Writing angles (left=%d, right=%d)", motors.left, motors.right));
			serial.write(1, motors.left, motors.right);

		}
	}

	/**
	 * Method for setting the Robot's {@link State}
	 * 
	 * @param state
	 */
	public void setState(State state) {
		this.state = state;
	}

	public State getState() {
		return this.state;
	}

}
