package net.talentum.jackie.system;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import net.talentum.jackie.comm.Commander;
import net.talentum.jackie.comm.ConsoleReader;
import net.talentum.jackie.comm.I2CCommunicator;
import net.talentum.jackie.comm.ImageServer;
import net.talentum.jackie.comm.TextInputProcessor;
import net.talentum.jackie.image.supplier.ImageSupplier;
import net.talentum.jackie.image.supplier.LocalWebcamImageSupplier;
import net.talentum.jackie.image.supplier.ServerImageSupplier;
import net.talentum.jackie.robot.Robot;
import net.talentum.jackie.robot.state.State;
import net.talentum.jackie.tools.FileChangedAutoReloadingStrategy;

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

	private static ExecutorService executor = Executors
			.newCachedThreadPool(new BasicThreadFactory.Builder().namingPattern("MainExecutor-%d").build());

	private static I2CCommunicator i2c;
	private static Commander commander;
	private static TextInputProcessor textInputProcessor;
	private static ConsoleReader consoleReader;

	public static Robot robot;
	private static ImageSupplier imageSupplier;
	private static ImageServer imageServer;
	private static AtomicBoolean running = new AtomicBoolean(true);

	/**
	 * Number of {@link State#run()} method runs
	 */
	public static AtomicInteger runs = new AtomicInteger(0);

	public static void run(String[] args) {
		registerShutdownHook();

		// initialize configuration manager
		ConfigurationManager.init();

		Run.loadOpenCV();

		// create robot
		System.out.println("Setting up control classes");
		i2c = new I2CCommunicator();
		commander = new Commander(i2c);
		textInputProcessor = new TextInputProcessor(commander);
		consoleReader = new ConsoleReader(textInputProcessor);

		System.out.println("Creating robot");
		robot = new Robot(commander);
		ConfigurationManager.setReloadedListener(robot::configurationReloaded);

		// create image supplier
		imageSupplier = new ServerImageSupplier("localhost");
		//imageSupplier = new LocalWebcamImageSupplier();

		robot.setImageSupplier(imageSupplier);

		// start webcam server
		System.out.println("Starting webcam server");
		imageServer = new ImageServer(imageSupplier);
		imageServer.start();

		// start ConsoleReader
		consoleReader.start();

		System.out.println("Running...");

		// run robot's cycle infinitely
		robot.init();
		robot.start();

		// monitor running
		while (running.get()) {
			int count = runs.getAndSet(0);
			System.out.println(String.format("Runs: %d", count));
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	private static void registerShutdownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				shutdown();
			}
		});
	}

	private static AtomicBoolean shutdownActionsPerformed = new AtomicBoolean(false);

	/**
	 * Initiates shutdown of the program. This methods returns immediately.
	 */
	public static void shutdown() {
		if (shutdownActionsPerformed.compareAndSet(false, true)) {
			executor.submit(Main::shutdownActions);
			System.exit(0);
		}
	}

	private static void shutdownActions() {

		System.out.println("Shutting down...");

		// stop main thread
		running.set(false);

		// stop auto configuration reloading
		FileChangedAutoReloadingStrategy.stopAll();

		// stop ImageServer
		imageServer.stop();

		// stop Robot
		executor.shutdown();
		robot.stop();

		// stop ConsoleReader
		consoleReader.stop();

		// stop ImageSupplier
		imageSupplier.close();

	}

}
