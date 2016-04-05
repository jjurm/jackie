package net.talentum.jackie.system;

import java.io.IOException;

import net.talentum.jackie.image.ImageSupplier;
import net.talentum.jackie.image.ServerImageSupplier;
import net.talentum.jackie.robot.Parameters;
import net.talentum.jackie.robot.Robot;
import net.talentum.jackie.robot.RobotRunThread;

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
		ImageSupplier imageSupplier;
		try {
			imageSupplier = new ServerImageSupplier(args[0]);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		robot.setImageSupplier(imageSupplier);

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
