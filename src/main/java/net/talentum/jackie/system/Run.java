package net.talentum.jackie.system;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import org.opencv.core.Core;

import net.talentum.jackie.comm.Commander;
import net.talentum.jackie.comm.I2CCommunicator;

/**
 * Primary runnable class.
 * 
 * @author JJurM
 */
public class Run {

	static AtomicBoolean loadedOpenCV = new AtomicBoolean(false);

	public static void main(String[] args) {

		run(args);

	}

	public static void loadOpenCV() {
		if (!loadedOpenCV.getAndSet(true))
			try {
				System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

			} catch (UnsatisfiedLinkError e) {
				System.out.println("OpenCV not available.");
			}
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
				RuntimeTests.testSerial();
				break;
			case "i2c":
				RuntimeTests.testI2C();
				break;
			case "i2ca":
			case "i2cm":
				I2CCommunicator i2c = new I2CCommunicator();
				Commander comm = new Commander(i2c);
				switch (task.charAt(3)) {
				case 'a':
					RuntimeTests.testI2CDevice(comm.i2c.deviceA);
					break;
				case 'm':
					RuntimeTests.testI2CDevice(comm.i2c.mpu6050);
					break;
				}
				break;
			case "webcam":
				RuntimeTests.testWebcam(args2);
				break;
			case "opencv":
				RuntimeTests.openCVWebcamTest(args2);
				break;
			case "us":
				RuntimeTests.testUltrasonicSensor(args2);
				break;
			default:
				if (!"".equals(task)) {
					System.out.println(String.format("'%s' is not a task.", args[0]));
				}
				System.out.println("Possible arguments are: run, serial, i2c, webcam");
				break;
			}
		}
	}

}
