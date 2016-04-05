package net.talentum.jackie.robot.state;

import java.util.concurrent.atomic.AtomicBoolean;

import net.talentum.jackie.system.ConfigurationManager;
import net.talentum.jackie.system.Main;

/**
 * A state that supposedly performs time-consuming computations. The method
 * {@link #runComputation()}, which is designed to be overridden by children, is
 * run in multiple threads concurrently. When any of the threads decides to end
 * the computation, {@link #stop()} method should be called.
 * 
 * @author JJurM
 */
public abstract class AbstractMultithreadedState implements State {

	public static final int COUNT = ConfigurationManager.getGeneralConfiguration().getInt("params/threads");
	private Thread[] threads = new Thread[COUNT];
	private AtomicBoolean started = new AtomicBoolean(false);

	Object monitor = new Object();
	public int running = 0;
	long lastRun = -1;
	long lastDuration = -1;

	@Override
	public final void run() {
		// set started
		started.set(true);

		// run threads
		Runnable r = this::computeThreadMethod;
		for (int i = 0; i < COUNT; i++) {
			threads[i] = new Thread(r);
			threads[i].start();
		}

		// wait for finish of all threads
		try {
			for (int i = 0; i < COUNT; i++) {
				threads[i].join();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Will end the computation threads and therefore subsequently release the
	 * thread executing the {@link #run()} method from waiting.
	 */
	protected void stop() {
		started.set(false);
	}

	/**
	 * A method performing the loop, destined to be run from the computing
	 * threads.
	 */
	public void computeThreadMethod() {
		while (started.get()) {

			boolean run = false;
			synchronized (monitor) {
				long now = System.currentTimeMillis();
				run = (running == 0) || (lastRun != -1 && lastDuration != -1 && now >= lastRun + lastDuration / COUNT);
				if (run) {
					running++;
					lastRun = now;
				}
			}

			if (run) {
				long start = System.currentTimeMillis();

				runComputation();

				long duration = System.currentTimeMillis() - start;
				Main.runs.incrementAndGet();

				synchronized (monitor) {
					running--;
					lastDuration = duration;
				}
			} else {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}
	}

	/**
	 * Children of {@link AbstractMultithreadedState} override this method to
	 * define its body. This method is then being called from more threads.
	 */
	public abstract void runComputation();

}
