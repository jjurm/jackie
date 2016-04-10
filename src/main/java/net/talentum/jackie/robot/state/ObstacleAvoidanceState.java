package net.talentum.jackie.robot.state;

import net.talentum.jackie.robot.Robot;
import net.talentum.jackie.tools.TimeTools;

public class ObstacleAvoidanceState extends AbstractState {

	public ObstacleAvoidanceState(Robot robot) {
		super(robot);
	}

	@Override
	public void run0() {
		robot.commander.writePropulsionMotors(100, 100);

		TimeTools.sleep(5000);
		
		robot.commander.writePropulsionMotors(90, 110);
		
		robot.setState(new NullState());
	}

}
