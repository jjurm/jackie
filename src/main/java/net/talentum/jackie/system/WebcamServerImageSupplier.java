package net.talentum.jackie.system;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.function.Supplier;

import javax.imageio.ImageIO;

/**
 * Webcam image supplier that takes images from a webcam server.
 * 
 * @author JJurM
 */
public class WebcamServerImageSupplier implements Supplier<BufferedImage> {

	public static Socket server;

	public WebcamServerImageSupplier(String serverName) throws UnknownHostException, IOException {
		int port = 4444;
		server = new Socket(serverName, port);
	}

	@Override
	public BufferedImage get() {
		try {
			return ImageIO.read(ImageIO.createImageInputStream(server.getInputStream()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
