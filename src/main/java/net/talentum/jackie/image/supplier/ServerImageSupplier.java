package net.talentum.jackie.image.supplier;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.Socket;

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

	public ServerImageSupplier(String serverName) {
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
		} catch (IOException e) {
			// ignore error, try to reconnect next time
		}
		return null;
	}

	@Override
	public void close() {
		
	}

	public static class Provider extends ImageSupplierProvider{
		
		public Provider(String name) {
			super(name);
		}

		@Override
		public ImageSupplier provide(String param) {
			return new ServerImageSupplier(param);
		}
		
	}

}
