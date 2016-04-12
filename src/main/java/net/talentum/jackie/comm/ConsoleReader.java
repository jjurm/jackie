package net.talentum.jackie.comm;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Class for reading input from an {@code InputStream}. Passes the input to the
 * given {@code TextInputProcessor}.
 * 
 * @author JJurM
 */
public class ConsoleReader implements Runnable {

	private AtomicBoolean run = new AtomicBoolean(false);
	private AtomicBoolean stopped = new AtomicBoolean(false);

	/**
	 * {@link TextInputProcessor} to pass input to.
	 */
	private TextInputProcessor processor;

	InputStreamReader isr;
	BufferedReader br;
	PrintWriter pw;

	Thread readerThread;

	/**
	 * Basic constructor.
	 * 
	 * @param processor
	 */
	public ConsoleReader(TextInputProcessor processor) {
		this.processor = processor;

		isr = new InputStreamReader(System.in);
		br = new BufferedReader(isr);
		pw = new PrintWriter(System.out, true);

		readerThread = new Thread(this);
	}

	/**
	 * Starts the thread.
	 */
	public void start() {
		if (run.compareAndSet(false, true)) {
			readerThread.start();
		}
	}

	/**
	 * Stops the reader and closes resources.
	 */
	public void stop() {
		if (run.get()) {
			stopped.set(true);
			/*try {
				isr.close();
			} catch (IOException e) {
				e.printStackTrace();
			}*/
		}
	}

	@Override
	public void run() {
		Thread.currentThread().setName("ConsoleReader");

		while (!stopped.get()) {

			try {
				processor.accept(br, pw);
			} catch (StreamCloseRequest e) {
				// Never close the console streams
			}

		}

	}

}
