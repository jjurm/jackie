package net.talentum.jackie.system;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.talentum.jackie.comm.I2CCommunicator;
import net.talentum.jackie.comm.SerialCommunicator;
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

}
