package net.talentum.jackie.moment;

/**
 * Contains collection of data measured by robot's sensors. SensorData is
 * included in every constructed {@link Moment}.
 * 
 * @author JJurM
 */
public class SensorData {

	/**
	 * Hidden constructor
	 */
	private SensorData() {
	}

	public static SensorData collect() {
		return new SensorData();
	}

}
