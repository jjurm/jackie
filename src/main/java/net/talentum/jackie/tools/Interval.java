package net.talentum.jackie.tools;

public class Interval {

	public final double a;
	public final double b;

	public Interval(double a, double b) {
		this.a = a;
		this.b = b;
	}
	
	public double getAverage() {
		return (a + b) / 2;
	}

}
