package net.talentum.jackie.moment.strategy;

import net.talentum.jackie.moment.Parameters;
import net.talentum.jackie.moment.Moment;
import net.talentum.jackie.moment.MomentData;
import net.talentum.jackie.moment.RobotInstruction;

public abstract class RobotStrategy {

	private String name;
	protected Parameters param;
	protected MomentData d;

	public RobotStrategy(String name, Parameters param) {
		this.name = name;
		this.param = param;
	}

	public void prepare(Moment moment) {
		d = new MomentData(moment, param);
	}

	public abstract RobotInstruction evaluate();

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return getName();
	}

}
