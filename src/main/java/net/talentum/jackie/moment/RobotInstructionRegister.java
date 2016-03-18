package net.talentum.jackie.moment;

import java.util.LinkedList;

/**
 * Holds history of {@code RobotInstruction}s.
 * 
 * @author JJurM
 */
public class RobotInstructionRegister {

	private LinkedList<RobotInstruction> robotInstructionList = new LinkedList<RobotInstruction>();
	private int maxHistory;

	public RobotInstructionRegister(int maxHistory) {
		this.maxHistory = maxHistory;
	}

	public void push(RobotInstruction robotInstruction) {
		robotInstructionList.push(robotInstruction);

		if (robotInstructionList.size() > maxHistory) {
			robotInstructionList.removeLast();
		}
	}

	public RobotInstruction getLastInstruction() {
		return robotInstructionList.peek();
	}

}
