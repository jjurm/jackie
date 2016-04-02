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
	public static int toRange(int val, int min, int max) {
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

	/**
	 * Parse integer value with default value if case of
	 * {@link NumberFormatException}.
	 * 
	 * @param val
	 * @param defaultVal
	 * @return
	 */
	public static int parseDefault(String val, int defaultVal) {
		try {
			return Integer.parseInt(val);
		} catch (NumberFormatException e) {
			return defaultVal;
		}
	}

	/**
	 * Checks if the value lies between {@code lower} and {@code upper}.
	 * 
	 * @param x
	 * @param lower
	 * @param upper
	 * @return
	 */
	public static boolean isBetween(long x, long lower, long upper) {
		return lower <= x && x <= upper;
	}

}
