package net.talentum.jackie.tools;

public class MathTools {

	public static double toRange(double val, double min, double max) {
		return Math.max(Math.min(val, max), min);
	}
	
}
