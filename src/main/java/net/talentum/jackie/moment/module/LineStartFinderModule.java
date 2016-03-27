package net.talentum.jackie.moment.module;

import net.talentum.jackie.moment.Situation;
import net.talentum.jackie.moment.MomentData;

/**
 * Module interface for finding the start of the line. Returns a
 * {@link Situation} representing the position and heading of the line.
 * 
 * @author JJurM
 */
public interface LineStartFinderModule {

	public Situation findLineStart(MomentData d);

}
