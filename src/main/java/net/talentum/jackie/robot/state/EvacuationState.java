package net.talentum.jackie.robot.state;

import java.awt.Point;

import net.talentum.jackie.image.SubtractingImageBallFinder;
import net.talentum.jackie.robot.Moment;
import net.talentum.jackie.robot.Robot;
import net.talentum.jackie.robot.RobotInstruction;

public class EvacuationState extends AbstractState {

	private SubtractingImageBallFinder subtractingImageBallFinder;
	
	public EvacuationState(SubtractingImageBallFinder subtractingImageBallFinder, Robot robot) {
		super(robot);
		this.subtractingImageBallFinder = subtractingImageBallFinder;
	}
	
	public final synchronized RobotInstruction process(Moment momentOff, Moment momentOn) {
		
		
		
		return null;
	}
	
	//TODO attach a light to robot 
	private void light(boolean on) {
		
	}
	
	@Override
	public State run0() {
		double heading = 0.0;
		
		light(false);
		Moment momentOff = robot.constructMoment();
		
		light(true);
		Moment momentOn = robot.constructMoment();
		light(false);
		
		Point maxPoint = null;
		if(momentOff != null && momentOn != null)
			maxPoint = subtractingImageBallFinder.find(momentOff.image, momentOn.image);
		
		if(maxPoint == null) {
			search();
		} else {
			pick();
		}
			
		return this;
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
