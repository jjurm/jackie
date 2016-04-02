package net.talentum.jackie.tools;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;

public class FileChangedAutoReloadingStrategy extends FileChangedReloadingStrategy {

	ExecutorService executor = Executors.newSingleThreadExecutor();
	
	@Override
	public void init() {
		super.init();
		
		executor.submit(() -> {
			try {
				while (true) {
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
	
}
