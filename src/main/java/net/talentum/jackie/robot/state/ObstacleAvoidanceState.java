package net.talentum.jackie.robot.state;

import net.talentum.jackie.robot.Robot;

public class ObstacleAvoidanceState extends AbstractState {

	public ObstacleAvoidanceState(Robot robot) {
		super(robot);
	}

	@Override
	public State run0() {
		robot.commander.writePropulsionMotors(120, 120);

		TimeTools.sleep(1000);
		 
		if (robot.commander.readUltrasonicSensor(0)>robot.commander.readUltrasonicSensor(1)){
		
		while (robot.commander.getGyroZ()> (-90)) {	
		robot.commander.writePropulsionMotors(60, 120);
				}
		robot.commander.writePropulsionMotors(120, 120);
		TimeTools.sleep(1000);
		
		while (robot.commander.getGyroZ()< (0)) {	
		robot.commander.writePropulsionMotors(120, 60);
				}
		while (robot.commander.ciarovysenzor)> daco) {	
				robot.commander.writePropulsionMotors(140,120);
				}
		} else
		{	
			while (robot.commander.getGyroZ()< (90)) {	
				robot.commander.writePropulsionMotors(120, 60);
						}
				robot.commander.writePropulsionMotors(120, 120);
				TimeTools.sleep(1000);
				
				while (robot.commander.getGyroZ()> (0)) {	
				robot.commander.writePropulsionMotors(60, 120);
						}
				while (robot.commander.ciarovysenzor)> daco) {	
						robot.commander.writePropulsionMotors(120,140);	
		}
		
		return new NullState();
	

	@Override
	public void begin() {

	}
	
}
