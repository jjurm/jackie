package net.talentum.jackie.system;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.talentum.jackie.serial.SerialCommunicator;
import net.talentum.jackie.tools.MathTools;

public class TestRun {

	public static void main(String[] args) {

		run(args);

	}

	public static void run(String[] args) {

		if (args.length == 0) {
			System.out.println("Possible arguments are: serial");
		} else {
			System.out.println("Running test: " + args[0]);
			switch (args[0]) {
			case "serial":
				testSerial();
				break;
			}
		}

	}

	static ExecutorService executor = Executors.newSingleThreadExecutor();
	
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
