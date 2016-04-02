package net.talentum.jackie.tools;

public class MathTools {

	/**
	 * Moves the given double into the specified range. Returns {@code min} for
	 * {@code val <= min} and {@code max} for {@code val >= max} (assuming
	 * {@code min <= max}).
	 * 
	 * @param val
	 *            value to move
	 * @param min
	 *            minimum
	 * @param max
	 *            maximum
	 * @return
	 */
	public static double toRange(double val, double min, double max) {
		return Math.max(Math.min(val, max), min);
	}

	/**
	 * Returns random integer in specified range (including {@code min} and
	 * {@code max}).
	 * 
	 * @param min
	 * @param max
	 * @return
	 */
	public static int randomRange(int min, int max) {
		int range = (max - min) + 1;
		return (int) (Math.random() * range) + min;
	}

	/**
	 * Returns {@code 1} for positive numbers, {@code -1} for negative numbers,
	 * {@code 0} otherwise.
	 * 
	 * @param d
	 * @return
	 */
	public static int side(double d) {
		if (d > 0)
			return 1;
		if (d < 0)
			return -1;
		return 0;
	}

}
