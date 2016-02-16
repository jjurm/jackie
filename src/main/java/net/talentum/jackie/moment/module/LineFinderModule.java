package net.talentum.jackie.moment.module;

import net.talentum.jackie.moment.MomentData;

public interface LineFinderModule {

	/**
	 * @param d
	 * @return {@code false}, when no point further can be found
	 */
	public boolean findNext(MomentData d);

}
