package net.talentum.jackie.system;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.function.Supplier;

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

	public static void main(String[] args) {

		System.out.println("Starting program");

		run(args);

		System.out.println("Ended");

	}

	public static void run(String[] args) {
		// create robot
		System.out.println("Creating robot");
		Parameters param = new Parameters();
		robot = new Robot(param);

		// create image supplier
		if (args.length < 1) {
			System.out.println("You must supply server name");
			return;
		}
		Supplier<BufferedImage> imageSupplier;
		try {
			imageSupplier = new WebcamServerImageSupplier(args[0]);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		robot.setWebcamImageSupplier(imageSupplier);

		// run the main robot's cycle
		robot.run();

	}

}
