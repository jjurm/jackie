package net.talentum.jackie.moment;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Deque;
import java.util.LinkedList;
import java.util.function.Supplier;

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
import net.talentum.jackie.state.LineFollowingState;
import net.talentum.jackie.state.State;

import org.apache.commons.lang3.tuple.ImmutablePair;

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
	

	private State state;
	
	public Robot(Parameters param) {
		this.param = param;
		
		

		// open serial communication
		serial = new SerialCommunicator();

		// create strategy
		// @formatter:off
		
		state = new LineFollowingState(param, this);
		
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
	 * Sets motors for given destination. The value is first inserted into the
	 * {@link PIDController} and then evaluated by a
	 * {@link MotorIntensityFunction}. Finally, obtained values are written to
	 * serial.
	 * 
	 * @param destination
	 */
	@Deprecated
	public void setMotors(Point destination) {

		// check if the result is valid
		if (destination == null || destination.equals(new Point(0, 0))) {
			return;
		}

		// get direction
		double direction = Math.PI / 2 - Math.atan2(destination.y, destination.x);
		//pid.getInput(-direction);

		// compute heading (= control variable of PID controller)
		//double heading = pid.performPID();

	}

	/**
	 * Contains the main cycle that is run by the robot.
	 */
	public void run() {
		while (true) {
			
			System.out.println("Running main robot cycle");

			ImmutablePair<Integer, Integer> motors = state.getMotorInstructions();

			// write to serial
			serial.write(1, motors.left, motors.right);

		}
	}

	/**
	 * Method for setting the Robot's {@link State}
	 * @param state
	 */
	public void setState(State state) {
		this.state = state;
	}
	
	public State getState(){
		return this.state;
	}
	
	

}
