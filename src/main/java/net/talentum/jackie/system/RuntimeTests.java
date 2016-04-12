package net.talentum.jackie.system;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.ds.v4l4j.V4l4jDriver;

import net.talentum.jackie.comm.Commander;
import net.talentum.jackie.comm.ConsoleReader;
import net.talentum.jackie.comm.Device;
import net.talentum.jackie.comm.I2CCommunicator;
import net.talentum.jackie.comm.SerialCommunicator;
import net.talentum.jackie.comm.TextInputProcessor;
import net.talentum.jackie.tools.MathTools;
import net.talentum.jackie.tools.TimeTools;

public class RuntimeTests {

	private static ExecutorService executor = Executors.newSingleThreadExecutor();

	static I2CCommunicator i2c;
	static Commander commander;
	static TextInputProcessor textInputProcessor;
	static PrintWriter pw;

	public static void init() {
		i2c = new I2CCommunicator();
		commander = new Commander(i2c);
		textInputProcessor = new TextInputProcessor(commander);
		pw = new PrintWriter(System.out);
	}

	public static void testSerial() {

		SerialCommunicator sc = new SerialCommunicator();
		executor.submit(new Runnable() {
			@Override
			public void run() {
				while (true) {
					String line = sc.readLine();
					System.out.println(line);
				}
			}
		});

		while (true) {
			sc.write(1, MathTools.randomRange(0, 180), MathTools.randomRange(0, 180));
			TimeTools.sleep(2000);
		}

	}

	public static void testI2C() {
		init();
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		try {
			while (true) {

				String[] parts = br.readLine().split("\\s");
				textInputProcessor.i2cArbitraryTransfer(parts, pw);

			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void openCVWebcamTest(String[] args) {
		if (args.length == 0) {
			System.out.println("openCVWebcamTest needs an argument.");
			return;
		}

		System.out.println("Loading OpenCV native library");
		Run.loadOpenCV();

		System.out.println("Creating VideoCapture");
		VideoCapture videoCapture = new VideoCapture(Integer.parseInt(args[0]));
		System.out.println("Created VideoCapture, loaded webcam " + args[0]);

		if (!videoCapture.open(0)) {
			System.out.println("Cannot open VideoCapture.");
			return;
		}
		System.out.println("Successfully opened VideoCapture.");
		System.out.println("Trying to read an image.");

		Mat image = new Mat();
		videoCapture.read(image);
		System.out.println("Image read.");
		if (image != null) {
			System.out.println("Image not null. Everything may run correctly.");
		}

	}

	public static void testWebcam(String[] args) {

		System.out.println("Testing connected webcams");

		if (args.length > 0) {
			switch (args[0]) {
			case "1":
				System.out.println("Setting V4l4j driver");
				Webcam.setDriver(new V4l4jDriver());
				break;
			}
		}

		System.out.println("Trying to establish a connection with the webcam library...");
		Webcam.getDefault();
		System.out.println("Success! Library is responding.");

		System.out.print("Number of recognized webcams: ");
		List<Webcam> webcams = Webcam.getWebcams();
		System.out.println(webcams.size());

		if (webcams.size() == 0) {
			System.out.println("No recognized webcam, exiting.");
			return;
		}

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
			return;
		}

		System.out.println("Returned image is an object :)");

	}

	public static void testUltrasonicSensor(String[] args) {
		init();
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		while (true) {
			
			try {
				String[] parts = br.readLine().split("\\s");
			} catch (IOException e) {
				e.printStackTrace();
			}
			textInputProcessor.readUltrasonic(args, pw);
			
		}

	}
	
	public static void testText() {
		init();
		ConsoleReader cr = new ConsoleReader(textInputProcessor);
		cr.start();
	}

}
