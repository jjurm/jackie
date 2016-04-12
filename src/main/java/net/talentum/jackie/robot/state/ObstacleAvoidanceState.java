package net.talentum.jackie.robot.state;

import net.talentum.jackie.robot.Robot;
import net.talentum.jackie.system.Config;
import net.talentum.jackie.tools.TimeTools;

public class ObstacleAvoidanceState extends AbstractState {

	public ObstacleAvoidanceState(Robot robot) {
		super(robot);
	}

	@Override
	public State run0() {
		int reverse = Config.get().getInt("params/speeds/reverse");
		robot.commander.writePropulsionMotors(reverse, reverse);

		TimeTools.sleep(Config.get().getInt("params/speeds/reverseTime"));
		
		int side = (robot.commander.readUltrasonicSensor(4) > robot.commander.readUltrasonicSensor(5)) ? 1 : -1;
		
		int rot = Config.get().getInt("params/speeds/rotation");
		robot.commander.writePropulsionMotors(90 - side * rot, 90 + side * rot);
		TimeTools.sleep(Config.get().getInt("params/speeds/rotationTime"));
		
		int normal = Config.get().getInt("params/speeds/normal");
		robot.commander.writePropulsionMotors(110 + side * normal, 110 - side * normal);
		TimeTools.sleep(2000);
		
		robot.commander.writePropulsionMotors((int) (110 + side * normal * 1.5), (int) (110 - side * normal * 1.5));
		TimeTools.sleep(500);
		
		return new LineFollowingState(robot);
	}

	@Override
	public void begin() {

	}

}
