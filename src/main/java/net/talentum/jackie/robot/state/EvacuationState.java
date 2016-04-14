package net.talentum.jackie.robot.state;

import java.awt.Point;
import java.awt.image.BufferedImage;

import net.talentum.jackie.comm.Commander;
import net.talentum.jackie.image.SubtractingImageBallFinder;
import net.talentum.jackie.robot.Robot;
import net.talentum.jackie.system.Config;
import net.talentum.jackie.tools.TimeTools;

public class EvacuationState extends AbstractState {

	private SubtractingImageBallFinder subtractingImageBallFinder;
	
	private boolean longSideNorth;
	
	private boolean leftCorner;

	public EvacuationState(Robot robot) {
		super(robot);
		this.subtractingImageBallFinder = new SubtractingImageBallFinder();
	}
	
	@Override
	public void begin() {
		int normalSpeed = Config.get().getInt("params/speeds/arm/normal");
		robot.commander.writePropulsionMotors(normalSpeed);
		
		robot.commander.writeMotor(Commander.MOTOR_CAMERA, Config.get().getInt("params/motorPositions/camera/middle"));
		robot.commander.writeMotor(Commander.MOTOR_ARM, Config.get().getInt("params/motorPositions/arm/middle"));
		
		TimeTools.sleep(1000);
		
		robot.commander.writePropulsionMotors(0);
		
		double front = robot.commander.readUltrasonicSensor(Commander.ULTRASONIC_FRONT);
		longSideNorth = front <= 95;
		
		double left = robot.commander.readUltrasonicSensor(Commander.ULTRASONIC_LEFT);
		double right = robot.commander.readUltrasonicSensor(Commander.ULTRASONIC_RIGHT);
		leftCorner = left < right;
	}

	@Override
	public State run0() {

		

		return this;
	}

	private Point findBall() {
		robot.commander.writeMotor(Commander.MOTOR_ARM, Config.get().getInt("params/motorPositions/arm/normal"));

		robot.commander.light(1, false);
		TimeTools.sleep(500);
		BufferedImage img1 = robot.getImage();

		robot.commander.light(1, true);
		TimeTools.sleep(500);
		BufferedImage img2 = robot.getImage();
		robot.commander.light(1, false);

		Point ball = null;
		if (img1 != null && img2 != null) {
			ball = subtractingImageBallFinder.find(img1, img2);
		}
		
		return ball;
	}

	/**
	 * Pick up a ball.
	 */
	private void pick() {

	}

	/**
	 * This method moves the robot in order to search for the ball.
	 */
	private void search() {

	}

}
