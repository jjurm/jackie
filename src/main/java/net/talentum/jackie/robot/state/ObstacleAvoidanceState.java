package net.talentum.jackie.robot.state;

import java.awt.Point;
import java.awt.image.BufferedImage;

import org.apache.commons.lang3.tuple.Triple;

import net.talentum.jackie.comm.Commander;
import net.talentum.jackie.module.impl.BlurImageModifierModule;
import net.talentum.jackie.robot.Robot;
import net.talentum.jackie.system.Config;
import net.talentum.jackie.tools.TimeTools;

public class ObstacleAvoidanceState extends AbstractState {

	BlurImageModifierModule blurrer = new BlurImageModifierModule();
	
	public ObstacleAvoidanceState(Robot robot) {
		super(robot);
	}

	@Override
	public State run0() {
		//int side = (robot.commander.readUltrasonicSensor(Commander.ULTRASONIC_LEFT) > robot.commander.readUltrasonicSensor(Commander.ULTRASONIC_RIGHT)) ? 1 : -1;
		int side = 1;
		
		// reverse
		int reverse = Config.get().getInt("params/speeds/reverse");
		robot.commander.writePropulsionMotors(reverse, reverse);
		TimeTools.sleep(500);
		
		// rotation
		int rot = Config.get().getInt("params/speeds/rotation");
		robot.commander.writePropulsionMotors((1 + side) / 2 * rot, (1 - side) / 2 * rot);
		TimeTools.sleep(1700);
		robot.commander.writePropulsionMotors(0);
		
		//robot.lineFollowingState.
		
		// normal
		int normal = 10;
		robot.commander.writePropulsionMotors(normal);
		TimeTools.sleep(2100);
		robot.commander.writePropulsionMotors(0);
		
		BufferedImage img = robot.getImage();
		img = blurrer.modify(img);
		robot.lineFollowingState.strategy.prepare(img);
		Triple<Point, Point, Point> p = robot.lineFollowingState.strategy.checkLine(new Point(img.getWidth() / 2, img.getHeight() - 10), -Math.PI / 2);
		if (p != null) {
			return robot.lineFollowingState;
		}
		
		double base = 2;
		robot.commander.writePropulsionMotors((int) ((- side + base) * normal), (int) ((side + base) * normal));
		TimeTools.sleep(1500);
		robot.commander.writePropulsionMotors(0);
		
		return robot.lineFollowingState;
	}

	@Override
	public void begin() {

	}

}
