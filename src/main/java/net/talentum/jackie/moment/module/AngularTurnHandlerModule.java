package net.talentum.jackie.moment.module;

import java.awt.Point;

import net.talentum.jackie.moment.MomentData;
import net.talentum.jackie.moment.Situation;

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
