package net.talentum.jackie.comm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;

import net.talentum.jackie.system.Main;

/**
 * Class for communication with clients and commanding system.
 * 
 * @author JJurM
 */
public class TextInputProcessor {
	Map<String, Command> commands = new HashMap<String, Command>();

	Commander commander;

	String lastCommand;

	/**
	 * Basic constructor
	 */
	public TextInputProcessor(Commander commander) {
		this.commander = commander;
		createCommands();
	}

	void createCommands() {
		Command c;

		commands.put("echo", (args, br, pw) -> pw.println("Echo"));

		// exit
		c = (args, br, pw) -> Main.shutdown();
		commands.put("exit", c);
		commands.put("quit", c);
		commands.put("q", c);

		// stream close
		commands.put("close", (args, br, pw) -> {
			throw new StreamCloseRequest();
		});

		// utility commands
		c = (args, br, pw) -> i2cArbitraryTransfer(args, pw);
		commands.put("i2c", c);
		commands.put("i", c);
		commands.put("us", (args, br, pw) -> readUltrasonic(args, pw));
		commands.put("test", (args, br, pw) -> pw.println(commander.testI2CAll()));
		commands.put("accel", (args, br, pw) -> pw.println(String.join(", ", Arrays.stream(commander.getAcceleration())
				.mapToObj(i -> String.valueOf(i)).toArray(s -> new String[s]))));
		commands.put("gyro", (args, br, pw) -> pw.println(String.join(", ",
				Arrays.stream(commander.getGyro()).mapToObj(i -> String.valueOf(i)).toArray(s -> new String[s]))));

		// robot commands
		commands.put("refresh", (args, br, pw) -> Main.robot.refresh());
		commands.put("begin", (args, br, pw) -> Main.robot.begin());

		commands.put("stop", (args, br, pw) -> commander.writePropulsionMotors(0));

	}

	public void i2cArbitraryTransfer(String[] args, PrintWriter pw) {
		if (args.length < 2) {
			pw.println("Syntax: i2c <device> <size> [command1] [command2] ...");
			return;
		}

		String deviceName = args[0];
		Device device = commander.i2c.getDevice(deviceName);

		if (device == null) {
			pw.println("No such device found");
			return;
		}

		int size = Integer.parseInt(args[1]);
		args = Arrays.copyOfRange(args, 2, args.length);

		Byte[] bs = Arrays.stream(args).map(p -> (byte) Integer.parseInt(p)).toArray(s -> new Byte[s]);
		byte[] command = ArrayUtils.toPrimitive(bs);

		int[] res = device.transfer(size, command);
		String[] r = new String[res.length];
		for (int i = 0; i < res.length; i++) {
			r[i] = String.valueOf(res[i]);
		}

		System.out.println(String.format("received: [%s]", String.join(", ", r)));
	}

	public void readUltrasonic(String[] args, PrintWriter pw) {
		if (args.length < 1) {
			pw.println("Syntax: us <index>");
		}

		int index = Integer.parseInt(args[0]);
		double dst = commander.readUltrasonicSensor(index);
		pw.println(String.format("%.2f cm", dst));

	}

	/**
	 * This will read command from given {@code InputStream} and perform needed
	 * actions. Another messages may be sent and received with
	 * {@code PrintWriter} and {@code InputStream}. This streams should not be
	 * in use by other threads while running this method.
	 * 
	 * @param br
	 *            Input stream
	 * @param pw
	 *            Output stream (PrintWriter must be set to auto-flush)
	 * @throws StreamCloseRequest
	 */
	public void accept(BufferedReader br, PrintWriter pw) throws StreamCloseRequest {
		try {
			String line = br.readLine();
			if (line == null) {
				throw new StreamCloseRequest();
			}
			line = line.trim();
			// if the string equals ".", use last command
			if (lastCommand != null && line.length() == 0) {
				line = lastCommand;
			} else {
				lastCommand = line;
			}

			performCommand(line, br, pw);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void performCommand(String line, BufferedReader br, PrintWriter pw) throws StreamCloseRequest {
		String[] parts = line.split("\\s+|\\+");
		String commandName = parts[0].trim();

		Command command = commands.get(commandName);
		if (command != null) {
			String[] args = Arrays.copyOfRange(parts, 1, parts.length);
			command.process(args, br, pw);
		}
	}

}
