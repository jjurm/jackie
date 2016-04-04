package net.talentum.jackie.system;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.function.Supplier;

import net.talentum.jackie.moment.Parameters;
import net.talentum.jackie.moment.Robot;
import net.talentum.jackie.moment.RobotRunThread;

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

	public static void run(String[] args) {
		// initialize configuration manager
		ConfigurationManager.init();

		// create robot
		System.out.println("Creating robot");
		Parameters param = new Parameters();
		robot = new Robot(param);
		ConfigurationManager.setReloadedListener(robot::configurationReloaded);

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

		// robot.setWebcamImageSupplier(new LocalWebcamImageSupplier());

		// run the processing threads
		for (int i = 0; i < RobotRunThread.COUNT; i++) {
			RobotRunThread thread = new RobotRunThread(robot);
			thread.start();
		}

		// monitor running
		while (true) {
			int count = RobotRunThread.runs.getAndSet(0);
			System.out.println(String.format("Runs: %d, running: %d", count, RobotRunThread.running));
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

}
