package net.talentum.jackie.image;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.imageio.ImageIO;

/**
 * Image supplier that takes images from a webcam server.
 * 
 * @author JJurM
 */
public class ServerImageSupplier implements ImageSupplier {

	public static Socket server;
	
	protected String serverName;
	protected int port;

	public ServerImageSupplier(String serverName) throws UnknownHostException, IOException {
		this.serverName = serverName;
		this.port = 4444;
	}

	@Override
	public BufferedImage getImage() {
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