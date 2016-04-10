package net.talentum.jackie.comm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import net.talentum.jackie.system.Main;

/**
 * Class for communication with clients and commanding system.
 * 
 * @author JJurM
 */
public class TextInputProcessor {
	Map<String, Command> commands = new HashMap<String, Command>();

	Commander commander;
	
	/**
	 * Basic constructor
	 */
	public TextInputProcessor(Commander commander) {
		this.commander = commander;
		createCommands();
	}

	void createCommands() {
		// exit
		Command exit = (args, br, pw) -> Main.shutdown();
		commands.put("exit", exit);
		commands.put("quit", exit);
		commands.put("q", exit);

		// stream close
		commands.put("close", (args, br, pw) -> {
			throw new StreamCloseRequest();
		});
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
			String[] parts = line.trim().split("\\s+");
			String commandName = parts[0].trim();

			Command command = commands.get(commandName);
			if (commandName.length() == 0) {
				// ignore empty line
			} else if (command != null) {
				String[] args = Arrays.copyOfRange(parts, 1, parts.length);
				command.process(args, br, pw);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
