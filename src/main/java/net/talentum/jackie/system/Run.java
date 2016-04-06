package net.talentum.jackie.system;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.github.sarxos.webcam.Webcam;

import net.talentum.jackie.comm.I2CCommunicator;
import net.talentum.jackie.comm.SerialCommunicator;
import net.talentum.jackie.image.LocalWebcamImageSupplier;
import net.talentum.jackie.tools.MathTools;

public class Run {

	static ExecutorService executor = Executors.newSingleThreadExecutor();

	public static void main(String[] args) {

		run(args);

	}

	public static void run(String[] args) {

		String task;
		if (args.length == 0) {
			task = "";
		} else {
			task = args[0].toLowerCase();
			System.out.println("Running program: " + task);
			String[] args2 = Arrays.copyOfRange(args, 1, args.length);
			switch (task) {
			case "run":
				Main.run(args2);
				break;
			case "serial":
				testSerial();
				break;
			case "i2c":
				testI2C();
				break;
			case "camera":
				testCamera(args2);
				break;
			default:
				if (!"".equals(task)) {
					System.out.println(String.format("'%s' is not a task.", args[0]));
				}
				System.out.println("Possible arguments are: run, serial, i2c");
				break;
			}
		}

	}

	public static void testSerial() {

		SerialCommunicator sc = new SerialCommunicator();
		executor.submit(() -> {
			while (true) {
				String line = sc.readLine();
				System.out.println(line);
			}
		});
		try {
			while (true) {
				sc.write(1, MathTools.randomRange(0, 180), MathTools.randomRange(0, 180));
				Thread.sleep(2000);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	public static void testI2C() {

		I2CCommunicator i2c = new I2CCommunicator();

		while (true) {
			int n = MathTools.randomRange(0, 255);
			int res = i2c.cTest(i2c.arduino, n);
			System.out.println(String.format("sent: %d, received: %d", n, res));
		}

	}

	public static void testCamera(String[] args) {

		System.out.println("Testing connected webcams");

		if (args.length > 0 && args[0].equalsIgnoreCase("1")) {
			System.out.println("Setting V4l4j driver");
			LocalWebcamImageSupplier.setV4l4jDriver();
		}

		System.out.println("Trying to establish a connection with the webcam library...");
		Webcam.getDefault();
		System.out.println("Success! Library is responding.");

		System.out.print("Number of recognized webcams: ");
		List<Webcam> webcams = Webcam.getWebcams();
		System.out.println(webcams.size());

		System.out.println("List of webcams:");
		for (Webcam webcam : webcams) {
			System.out.println("    " + webcam.getName());
		}

		Webcam def = Webcam.getDefault();
		System.out.println("Using default webcam: " + def.getName());

		System.out.println("Opening webcam...");
		def.open();
		System.out.println("Opened!");

		System.out.print("Taking image... ");
		BufferedImage img = def.getImage();
		System.out.println("done!");

		if (img == null) {
			System.out.println("Returned image is null :(");
		} else {
			System.out.println("Returned image is an object :)");
		}

	}

}
