package net.talentum.jackie.robot.state;

import net.talentum.jackie.robot.Robot;
import net.talentum.jackie.tools.TimeTools;

public class ObstacleAvoidanceState implements State {

	Robot robot;

	public ObstacleAvoidanceState(Robot robot) {
		this.robot = robot;
	}

	@Override
	public void run() {

		robot.writeMotors(100, 100);

		TimeTools.sleep(5000);
		
		robot.writeMotors(90, 110);
		
		robot.setState(new NullState());
		
	}

}