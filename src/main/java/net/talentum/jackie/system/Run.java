package net.talentum.jackie.system;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.talentum.jackie.comm.SerialCommunicator;
import net.talentum.jackie.tools.MathTools;

public class Run {

	static ExecutorService executor = Executors.newSingleThreadExecutor();
	
	public static void main(String[] args) {

		run(args);

	}

	public static void run(String[] args) {

		if (args.length == 0) {
			System.out.println("Possible arguments are: run, serial");
		} else {
			System.out.println("Running program: " + args[0]);
			String[] args2 = Arrays.copyOfRange(args, 1, args.length);
			switch (args[0]) {
			case "run":
				Main.run(args2);
				break;
			case "serial":
				testSerial();
				break;
			case "i2c":
				testI2C();
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
		
		// todo
		
	}

}
