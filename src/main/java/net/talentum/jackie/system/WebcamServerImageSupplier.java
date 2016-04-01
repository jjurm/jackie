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
	
	protected String serverName;
	protected int port;

	public WebcamServerImageSupplier(String serverName) throws UnknownHostException, IOException {
		this.serverName = serverName;
		this.port = 4444;
	}

	@Override
	public BufferedImage get() {
		try {
			server = new Socket(serverName, port);
			BufferedImage img = ImageIO.read(ImageIO.createImageInputStream(server.getInputStream()));
			server.close();
			return img;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
