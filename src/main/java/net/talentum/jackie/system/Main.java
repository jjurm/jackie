package net.talentum.jackie.system;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import net.talentum.jackie.image.ServerImageSupplier;
import net.talentum.jackie.robot.Parameters;
import net.talentum.jackie.robot.Robot;
import net.talentum.jackie.robot.state.State;

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

	private static ExecutorService executor = Executors.newCachedThreadPool();
	private static Robot robot;

	/**
	 * Number of {@link State#run()} method runs
	 */
	public static AtomicInteger runs = new AtomicInteger(0);

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

		robot.setImageSupplier(new ServerImageSupplier(args[0]));
		// robot.setWebcamImageSupplier(new LocalWebcamImageSupplier());

		// monitor running
		executor.submit(new Runnable() {
			@Override
			public void run() {
				while (true) {
					int count = runs.getAndSet(0);
					System.out.println(String.format("Runs: %d", count));
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});

		// run robot's cycle infinitely
		robot.runCycle();

	}

}
