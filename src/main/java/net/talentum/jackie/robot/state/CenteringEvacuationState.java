package net.talentum.jackie.robot.state;

import java.awt.Point;
import java.awt.image.BufferedImage;

import net.talentum.jackie.comm.Commander;
import net.talentum.jackie.image.SubtractingImageBallFinder;
import net.talentum.jackie.module.MotorIntensityFunction;
import net.talentum.jackie.module.impl.BasicMotorIntensityFunction;
import net.talentum.jackie.robot.Robot;
import net.talentum.jackie.system.Config;
import net.talentum.jackie.tools.TimeTools;

public class CenteringEvacuationState extends AbstractState {

	MotorIntensityFunction mif;
	private SubtractingImageBallFinder subtractingImageBallFinder;

	public CenteringEvacuationState(Robot robot) {
		super(robot);
		mif = new BasicMotorIntensityFunction();
		subtractingImageBallFinder = new SubtractingImageBallFinder();
	}

	@Override
	public void begin() {
		int normalSpeed = Config.get().getInt("params/speeds/arm/normal");
		robot.commander.writePropulsionMotors(normalSpeed);

		robot.commander.writeMotor(Commander.MOTOR_CAMERA, Config.get().getInt("params/motorPositions/camera/middle"));
		robot.commander.writeMotor(Commander.MOTOR_ARM, Config.get().getInt("params/motorPositions/arm/middle"));

		TimeTools.sleep(1000);

		robot.commander.writePropulsionMotors(0);
	}

	public void goToCenter() {

		double right, left;
		while (true) {

			left = robot.commander.readUltrasonicSensor(Commander.ULTRASONIC_LEFT);
			right = robot.commander.readUltrasonicSensor(Commander.ULTRASONIC_RIGHT);

			if (robot.commander.readUltrasonicSensor(Commander.ULTRASONIC_FRONT) < 60 && left < 40 && right < 40) {
				break;
			}

			double heading = right - left;
			mif.getMotors(heading);

		}

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
			ball.translate(- subtractingImageBallFinder.result.getWidth() / 2, 0);
			ball.y = subtractingImageBallFinder.result.getHeight() - ball.y;
		}

		return ball;
	}

	@Override
	public State run0() {
		int speed;
		
		goToCenter();

		while (true) {

			Point ball = findBall();

			if (ball != null) {
				speed = Config.get().getInt("params/speeds/normal");
				int side = ball.x > 0 ? 1 : -1;
				robot.commander.writePropulsionMotors(speed * side, - speed * side);
				
				
				
			} else {
				speed = Config.get().getInt("params/speeds/normal");
				robot.commander.writePropulsionMotors(-speed, speed);
				TimeTools.sleep(600);
				robot.commander.writePropulsionMotors(0);
				continue;
			}

		}

	}

}
