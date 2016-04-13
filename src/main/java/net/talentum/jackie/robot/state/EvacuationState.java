package net.talentum.jackie.robot.state;

import java.awt.Point;
import java.awt.image.BufferedImage;

import net.talentum.jackie.image.SubtractingImageBallFinder;
import net.talentum.jackie.robot.Robot;
import net.talentum.jackie.tools.TimeTools;

public class EvacuationState extends AbstractState {

	private SubtractingImageBallFinder subtractingImageBallFinder;
	
	public EvacuationState(SubtractingImageBallFinder subtractingImageBallFinder, Robot robot) {
		super(robot);
		this.subtractingImageBallFinder = subtractingImageBallFinder;
	}
	
	@Override
	public State run0() {
		double heading = 0.0;
		
		robot.commander.light(1, false);
		BufferedImage img1 = robot.getImage();
		
		robot.commander.light(1, true);
		TimeTools.sleep(500);
		BufferedImage img2 = robot.getImage();
		robot.commander.light(1, false);
		
		Point maxPoint = null;
		if(img1 != null && img2 != null) {
			maxPoint = subtractingImageBallFinder.find(img1, img2);
		}
		
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
