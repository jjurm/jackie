package net.talentum.jackie.image;

import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamException;
import com.github.sarxos.webcam.ds.v4l4j.V4l4jDriver;

/**
 * Image supplier that takes images from the default webcam.
 * 
 * @author JJurM
 */
public class LocalWebcamImageSupplier implements ImageSupplier {

	private Webcam webcam;

	public LocalWebcamImageSupplier() {

		// set Webcam driver
		Webcam.setDriver(new V4l4jDriver());

		try {
			webcam = Webcam.getDefault(5, TimeUnit.SECONDS);
		} catch (WebcamException | TimeoutException e) {
			e.printStackTrace();
			return;
		}
		webcam.open();
	}

	@Override
	public BufferedImage getImage() {
		return webcam.getImage();
	}

}
