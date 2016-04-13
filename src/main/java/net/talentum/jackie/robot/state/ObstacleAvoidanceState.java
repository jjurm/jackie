package net.talentum.jackie.robot.state;

import net.talentum.jackie.comm.Commander;
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
		
		int side = (robot.commander.readUltrasonicSensor(Commander.ULTRASONIC_LEFT) > robot.commander.readUltrasonicSensor(Commander.ULTRASONIC_RIGHT)) ? 1 : -1;
		
		int rot = Config.get().getInt("params/speeds/rotation");
		robot.commander.writePropulsionMotors(- side * rot, side * rot);
		TimeTools.sleep(Config.get().getInt("params/speeds/rotationTime"));
		
		int normal = Config.get().getInt("params/speeds/normal");
		robot.commander.writePropulsionMotors((side + 1) * normal, (side - 1) * normal);
		TimeTools.sleep(2000);
		
		return new LineFollowingState(robot);
	}

	@Override
	public void begin() {

	}

}
