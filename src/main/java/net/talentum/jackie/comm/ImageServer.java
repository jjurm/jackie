package net.talentum.jackie.comm;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.imageio.ImageIO;

import net.talentum.jackie.image.supplier.ImageSupplier;
import net.talentum.jackie.image.supplier.LocalWebcamImageSupplier;

/**
 * Class providing network access to the given {@link ImageSupplier}. Method
 * {@link #start()} will start the server on a separate thread. Stop it with the
 * {@link #stop()} method.
 * 
 * @author padr31
 */
public class ImageServer implements Runnable {

	private ExecutorService executor = Executors.newCachedThreadPool();
	private ServerSocket serverSocket;

	private Thread serverThread;

	private ImageSupplier imageSupplier;

	private int portNumber = 4444;

	private AtomicBoolean startedServer = new AtomicBoolean(false);

	public static void main(String[] args) {
		System.out.println("Running Image server");

		ImageSupplier imageSupplier = new LocalWebcamImageSupplier();
		final ImageServer imageServer = new ImageServer(imageSupplier);
		imageServer.start();

		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				imageServer.stop();
			}
		}));
	}

	public ImageServer(ImageSupplier imageSupplier) {
		this.imageSupplier = imageSupplier;
		serverThread = new Thread(this);
		serverThread.setName("ImageServerThread");
	}

	public void start() {
		if (!startedServer.compareAndSet(false, true))
			return;
		serverThread.start();
	}

	public void stop() {
		if (!startedServer.compareAndSet(true, false))
			return;
		try {
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		serverThread.interrupt();
	}

	@Override
	public void run() {
		try {
			try {
				serverSocket = new ServerSocket(portNumber);
			} catch (IOException e) {
				e.printStackTrace();
			}

			while (startedServer.get()) {
				try {
					Socket client = serverSocket.accept();

					executor.submit(() -> {
						try {
							BufferedImage image = null;
							while (image == null) {
								image = imageSupplier.getImage();
							}
							ImageIO.write(image, "JPG", client.getOutputStream());
							client.close();
						} catch (Exception e) {
							e.printStackTrace();
						}
					});

				} catch (IOException e) {
					if (startedServer.get()) {
						e.printStackTrace();
					}
				}
			}
		} catch (Exception e) {
			// ignore
		}
	}

}
