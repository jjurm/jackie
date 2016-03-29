package net.talentum.jackie.system;

import net.talentum.jackie.serial.SerialCommunicator;

public class TestRun {

	public static void main(String[] args) {

		run(args);

	}

	public static void run(String[] args) {

		if (args.length == 0) {
			System.out.println("Possible arguments are: serial");
		} else {
			switch (args[0]) {
			case "serial":
				testSerial();
				break;
			}
		}

	}

	public static void testSerial() {

		SerialCommunicator sc = new SerialCommunicator();

		try {
			while (true) {
				sc.write(1, 80, 100);
				Thread.sleep(1000);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
