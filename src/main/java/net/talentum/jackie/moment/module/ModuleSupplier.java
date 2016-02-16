package net.talentum.jackie.moment.module;

import net.talentum.jackie.moment.MomentData;

public interface ModuleSupplier<M> {

	public M create(MomentData d);

}
