package net.talentum.jackie.system;

/**
 * Convenience class for accessing the configuration. Contains mainly the
 * entries that are requested very often.
 * 
 * <p>
 * Values of the entries are stored in the class, but are reloaded from the
 * configuration upon call of {@link #reload()}.
 * </p>
 * 
 * @author JJurM
 */
public class Config {

	public static int movedst;

	/**
	 * Method to reload configuration
	 */
	public static void reload() {
		movedst = ConfigurationManager.getGeneralConfiguration().getInt("params/movedst");
	}

}
