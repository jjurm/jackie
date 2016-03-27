package net.talentum.jackie.moment.module;

import net.talentum.jackie.moment.MomentData;

/**
 * Module interface for finding new line points.
 * 
 * @author JJurM
 */
public interface LineFinderModule {

	/**
	 * @param d
	 * @return {@code false}, when no further point can be found
	 */
	public boolean findNext(MomentData d);

}
