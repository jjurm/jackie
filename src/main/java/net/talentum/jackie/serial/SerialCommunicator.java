package net.talentum.jackie.serial;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import com.pi4j.io.serial.Serial;
import com.pi4j.io.serial.SerialDataEvent;
import com.pi4j.io.serial.SerialDataEventListener;
import com.pi4j.io.serial.SerialFactory;

/**
 * Class responsible for serial communication. Receives and processes input
 * commands and is capable of writing into the output stream.
 * 
 * <p>
 * <b>Command syntax</b><br/>
 * Each command consists of integers separated by spaces. Commands are
 * terminated by end of line, i.e. the {@code "\n"} character. The command is
 * specified by the first integers, the following are arguments.
 * </p>
 * 
 * <p>
 * <b>List of commands</b><br/>
 * </p>
 * <table border="1" cellspacing="0">
 * <tr>
 * <th>Command #</th>
 * <th>Description</th>
 * <th>List of arguments</th>
 * <th>Examples</th>
 * </tr>
 * <tr>
 * <td><b>1</b></td>
 * <td>Set motor speed</td>
 * <td>
 * <ul>
 * <li>left motor</li>
 * <li>right motor</li>
 * </ul>
 * </td>
 * <td>{@code 1 100 80}</td>
 * </tr>
 * </table>
 * 
 * @author JJurM
 */
public class SerialCommunicator {

	final Serial serial;

	/**
	 * A buffer with stored received strings.
	 */
	ConcurrentLinkedDeque<String> deque = new ConcurrentLinkedDeque<String>();

	ExecutorService executor = Executors.newSingleThreadExecutor();

	/**
	 * Basic constructor. Creates and opens serial port.
	 */
	public SerialCommunicator() {

		// create serial
		serial = SerialFactory.createInstance();
		serial.addListener(new Listener());

		open();

		// run executor
		executor.submit(() -> {
			while (true) {
				String line = readLine();
				process(line);
			}
		});
	}

	private void open() {
		// open serial
		try {
			serial.open(Serial.DEFAULT_COM_PORT, 38400);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Handles received command.
	 * 
	 * @param line
	 * @see SerialCommunicator
	 */
	public void process(String line) {

	}

	/**
	 * Writes command into serial.
	 * 
	 * @param command
	 * @see SerialCommunicator
	 */
	public void write(Integer... command) {
		String line = Arrays.stream(command).map(n -> String.valueOf(n)).collect(Collectors.joining(" "));
		try {
			serial.writeln(line + ";");
		} catch (IllegalStateException | IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Waits until a whole line is available and returns the line.
	 * 
	 * @return
	 */
	public String readLine() {
		StringBuffer buffer = new StringBuffer();
		while (true) {

			String str;
			while (true) {
				str = deque.pollFirst();
				if (str != null) {
					break;
				}
				synchronized (deque) {
					try {
						deque.wait();
					} catch (InterruptedException e) {
						// do nothing
					}
				}
			}
			;

			int index = str.indexOf("\n");
			if (index == -1) {
				buffer.append(str);
			} else {
				buffer.append(str.substring(0, index));

				String remainder = str.substring(index + 1);
				if (remainder.length() > 0)
					deque.addFirst(remainder);

				return buffer.toString();
			}

		}
	}

	/**
	 * This class is supplied as a listener for the {@link Serial}.
	 * 
	 * @author JJurM
	 */
	class Listener implements SerialDataEventListener {
		@Override
		public void dataReceived(SerialDataEvent event) {
			try {
				String str = event.getAsciiString();
				synchronized (deque) {
					deque.addLast(str);
					deque.notify();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

}
