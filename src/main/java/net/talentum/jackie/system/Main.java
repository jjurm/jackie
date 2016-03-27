package net.talentum.jackie.system;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamException;

import net.talentum.jackie.moment.Parameters;
import net.talentum.jackie.moment.Robot;

/**
 * Runnable class.
 * 
 * <p>
 * This class is run normally, when controlling robot.
 * </p>
 * 
 * @author JJurM
 */
public class Main {

	public static Robot robot;
	public static Webcam webcam;

	public static void main(String[] args) {

		run(args);

	}

	public static void run(String[] args) {
		Parameters param = new Parameters();

		// create robot
		robot = new Robot(param);

		// create and open webcam
		try {
			webcam = Webcam.getDefault(5, TimeUnit.SECONDS);
		} catch (WebcamException | TimeoutException e) {
			e.printStackTrace();
			return;
		}
		webcam.open();
		robot.setWebcamImageSupplier(() -> webcam.getImage());

		// run the main robot's cycle
		robot.run();

	}

}
