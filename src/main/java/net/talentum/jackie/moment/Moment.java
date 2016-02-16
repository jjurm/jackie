package net.talentum.jackie.moment;

import java.awt.image.BufferedImage;

public class Moment {

	BufferedImage image;
	SensorData sensorData;
	
	public Moment(BufferedImage image, SensorData sensorData) {
		this.image = image;
		this.sensorData = sensorData;
	}
	
}
