package net.talentum.jackie.moment;

import java.awt.image.BufferedImage;

public class Moment {

	public final BufferedImage image;
	public final SensorData sensorData;
	
	public Moment(BufferedImage image, SensorData sensorData) {
		this.image = image;
		this.sensorData = sensorData;
	}
	
}
