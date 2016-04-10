package net.talentum.jackie.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

public class FileChangedAutoReloadingStrategy extends FileChangedReloadingStrategy {

	static ExecutorService executor = Executors.newCachedThreadPool(new BasicThreadFactory.Builder().namingPattern("FileMonitor-%d").build());
	static AtomicBoolean stopped = new AtomicBoolean(false);
	
	static List<Thread> threads = new ArrayList<Thread>();
	
	@Override
	public void init() {
		super.init();
		
		executor.submit(() -> {
			try {
				while (!stopped.get()) {
					Thread.sleep(refreshDelay);
					if (reloadingRequired()) {
						configuration.reload();
					}
				}
			} catch (Exception e) {
				return;
			}
		});
	}
	
	public static void stopAll() {
		executor.shutdown();
		stopped.set(true);
	}
	
}
