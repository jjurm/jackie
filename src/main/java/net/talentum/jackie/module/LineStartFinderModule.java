package net.talentum.jackie.module;

import net.talentum.jackie.robot.MomentData;
import net.talentum.jackie.robot.Situation;

/**
 * Module interface for finding the start of the line. Returns a
 * {@link Situation} representing the position and heading of the line.
 * 
 * @author JJurM
 */
public interface LineStartFinderModule {

	public Situation findLineStart(MomentData d);

}
