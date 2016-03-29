package net.talentum.jackie.tools;

public class MathTools {

	public static double toRange(double val, double min, double max) {
		return Math.max(Math.min(val, max), min);
	}

	public static int randomRange(int min, int max) {
		int range = (max - min) + 1;
		return (int) (Math.random() * range) + min;
	}

}
