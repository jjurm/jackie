package net.talentum.jackie.moment;

import java.awt.image.BufferedImage;

import net.talentum.jackie.system.StrategyComparatorPreview;

/**
 * This class represents one moment in time, one state in which the robot was.
 * Moment consists of image captured by webcam and sensor data that were
 * measured at the same time.
 * 
 * <p>
 * When new moment is constructed, it is being processed and evaluated into
 * {@link RobotInstruction}. Moment can be processed more times (with different
 * strategies) - this is used when comparing strategies with
 * {@link StrategyComparatorPreview}.
 * </p>
 * 
 * @author JJurM
 */
public class Moment {

	public final BufferedImage image;
	public final SensorData sensorData;

	public Moment(BufferedImage image, SensorData sensorData) {
		this.image = image;
		this.sensorData = sensorData;
	}

}
