package net.talentum.jackie.image;

import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamException;

/**
 * Image supplier that takes images from the default webcam.
 * 
 * @author JJurM
 */
public class LocalWebcamImageSupplier implements ImageSupplier {

	private Webcam webcam;

	public LocalWebcamImageSupplier() {
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
