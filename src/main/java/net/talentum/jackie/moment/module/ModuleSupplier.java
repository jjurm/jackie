package net.talentum.jackie.moment.module;

import net.talentum.jackie.moment.MomentData;

/**
 * Supplier that produces modules. Used for modules that are created dynamically.
 * 
 * @author JJurM
 *
 * @param <M> module to supply
 */
public interface ModuleSupplier<M> {

	/**
	 * Return new module
	 * 
	 * @param d
	 * @return
	 */
	public M create(MomentData d);

}
