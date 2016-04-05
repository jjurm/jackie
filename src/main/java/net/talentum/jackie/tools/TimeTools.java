package net.talentum.jackie.tools;

public class TimeTools {

	/**
	 * Sleep for specified number of milliseconds.
	 * 
	 * @param ms
	 */
	public static void sleep(long ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}
