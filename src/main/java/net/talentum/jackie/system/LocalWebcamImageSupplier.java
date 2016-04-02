package net.talentum.jackie.system;

import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamException;

/**
 * Image supplier that takes images from the default webcam.
 * 
 * @author JJurM
 */
public class LocalWebcamImageSupplier implements Supplier<BufferedImage> {

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
	public BufferedImage get() {
		return webcam.getImage();
	}

}
