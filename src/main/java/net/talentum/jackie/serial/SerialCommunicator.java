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

public class SerialCommunicator {

	final Serial serial;
	final StringBuffer buffer = new StringBuffer();
	Object monitor;

	ConcurrentLinkedDeque<String> deque = new ConcurrentLinkedDeque<String>();

	ExecutorService executor = Executors.newSingleThreadExecutor();

	public SerialCommunicator() {

		// create serial
		serial = SerialFactory.createInstance();
		serial.addListener(new Listener());

		// run executor
		executor.submit(() -> {
			while (true) {
				String line = readLine();
				process(line);
			}
		});

	}

	public void open() {
		// open serial
		try {
			serial.open(Serial.DEFAULT_COM_PORT, 38400);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void process(String line) {

	}

	public void write(Integer... command) {
		String line = Arrays.stream(command).map(n -> String.valueOf(n)).collect(Collectors.joining(" "));
		try {
			serial.writeln(line);
		} catch (IllegalStateException | IOException e) {
			e.printStackTrace();
		}
	}

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
				deque.addFirst(str.substring(index));

				return buffer.toString();
			}

		}
	}

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
