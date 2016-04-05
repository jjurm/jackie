package net.talentum.jackie.module;

import java.awt.Point;

import net.talentum.jackie.robot.MomentData;
import net.talentum.jackie.robot.Situation;

/**
 * Module interface that takes care of angular turns.
 * 
 * @author JJurM
 */
public interface AngularTurnHandlerModule {

	/**
	 * @return {@link Situation} when an angular turn was detected and treated, {@code null} otherwise.
	 */
	public Situation detectAndProceed(MomentData d, Point expected, double direction);
	
}
