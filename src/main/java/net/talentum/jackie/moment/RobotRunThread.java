package net.talentum.jackie.moment;

import java.util.concurrent.atomic.AtomicInteger;

import net.talentum.jackie.system.ConfigurationManager;

public class RobotRunThread extends Thread {

	public static final int COUNT = ConfigurationManager.getGeneralConfiguration().getInt("params/threads");
	static AtomicInteger index = new AtomicInteger(0);
	public static AtomicInteger runs = new AtomicInteger(0);

	static Object monitor = new Object();
	public static int running = 0;
	static long lastRun = -1;
	static long lastDuration = -1;

	Robot robot;

	public RobotRunThread(Robot robot) {
		this.robot = robot;
	}

	@Override
	public void run() {
		Thread.currentThread().setName("RobotRunThread-" + index.getAndIncrement());
		while (true) {

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
				robot.runOnce();
				long duration = System.currentTimeMillis() - start;
				runs.incrementAndGet();

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

}
