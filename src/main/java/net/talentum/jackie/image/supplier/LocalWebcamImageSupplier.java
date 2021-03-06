package net.talentum.jackie.image.supplier;

import java.awt.Dimension;
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
	
	static {
		Webcam.setDriver(new V4l4jDriver());
	}

	public LocalWebcamImageSupplier() {
		try {
			webcam = Webcam.getDefault(5, TimeUnit.SECONDS);
		} catch (WebcamException | TimeoutException e) {
			e.printStackTrace();
			return;
		}
		if (webcam != null) {
			webcam.open();
		}
	}

	public LocalWebcamImageSupplier(Webcam webcam) {
		this.webcam = webcam;
		webcam.open();
	}

	@Override
	public BufferedImage getImage() {
		return webcam.getImage();
	}

	public void setViewSize(Dimension d) {
		webcam.setViewSize(d);
	}

	@Override
	public void close() {
		webcam.close();
	}

	public static class Provider extends ImageSupplierProvider {

		public Provider(String name) {
			super(name);
		}

		@Override
		public ImageSupplier provide(String param) {
			return new LocalWebcamImageSupplier();
		}

	}
}
