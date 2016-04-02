package net.talentum.jackie.system;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongUnaryOperator;

import org.apache.commons.configuration.AbstractHierarchicalFileConfiguration;
import org.apache.commons.configuration.CombinedConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.event.ConfigurationEvent;
import org.apache.commons.configuration.event.ConfigurationListener;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.apache.commons.configuration.tree.xpath.XPathExpressionEngine;

import net.talentum.jackie.tools.FileChangedAutoReloadingStrategy;

public class ConfigurationManager {

	/**
	 * This map stores configuration objects since they are constructed.
	 */
	private static Map<String, HierarchicalConfiguration> configurations;

	/**
	 * Runnable that will be called when the configuration has been reloaded.
	 */
	private static Runnable reloadedListener;

	private static CombinedConfiguration generalConfig;

	/**
	 * This method checks for missing or unreadable configuration files, builds
	 * associated configuration objects and finally builds general configuration
	 * file, throwing {@link ConfigurationException} in case of failure. In
	 * general configuration, keys of the configurations determine the path of
	 * their root nodes in the general configuration.
	 * 
	 * @throws ConfigurationException
	 */
	public static void init() {
		HierarchicalConfiguration.setDefaultExpressionEngine(new XPathExpressionEngine());

		try {
			buildConfigurations();
			buildGeneralConfig();
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	public static void setReloadedListener(Runnable listener) {
		reloadedListener = listener;
	}

	private static void buildConfigurations() throws ConfigurationException {
		configurations = new HashMap<String, HierarchicalConfiguration>();
		String key;
		AbstractHierarchicalFileConfiguration config;

		key = "params";
		config = new XMLConfiguration(checkFile(key, "config/params.xml", true));
		FileChangedReloadingStrategy reloadingStrategy = new FileChangedAutoReloadingStrategy();
		final int refreshDelay = 1000;
		reloadingStrategy.setRefreshDelay(refreshDelay);
		config.setReloadingStrategy(reloadingStrategy);
		config.addConfigurationListener(new ConfigurationListener() {
			AtomicLong lastUpdated = new AtomicLong(0);
			@Override
			public void configurationChanged(ConfigurationEvent event) {
				long now = System.currentTimeMillis();
				if (now - refreshDelay > lastUpdated.getAndUpdate(v -> now - refreshDelay > v ? now : v)) {
					System.out.println("Config changed: " + event.getPropertyName());
					Runnable listener = reloadedListener;
					if (listener != null) {
						listener.run();
					}
				}
			}
		});
		configurations.put(key, config);
	}

	private static void buildGeneralConfig() {
		generalConfig = new CombinedConfiguration();
		for (String key : configurations.keySet()) {
			generalConfig.addConfiguration(getConfiguration(key), key, key);
		}
	}

	private static File checkFile(String key, String filename, boolean required) throws ConfigurationException {
		File file = new File(filename);

		// check if the file exists
		if (file.exists()) {
			// check if the file can be read
			if (file.canRead()) {
				// expected point
			} else {
				// file could not be read
				if (required) {
					// last configuration file must be readable
					System.out.println(String.format(
							"config: Existing required configuration file ('%s') is not readable: %s", key, filename));
					throw new ConfigurationException(String.format("unreadable file: %s", filename));
				} else {
					System.out.println(String.format("config: Existing configuration file ('%s') is not readable: %s",
							key, filename));
				}
			}
		} else {
			// file does not exist
			if (required) {
				// last configuration must exist
				System.out.println(
						String.format("config: Required configuration file ('%s') is missing: %s", key, filename));
				throw new ConfigurationException(String.format("missing file: %s", filename));
			} else {
				System.out.println(String.format("config: Configuration file ('%s') is missing: %s", key, filename));
			}
		}
		return file;
	}

	/**
	 * This will search in a map for the name and create configuration from the
	 * path.
	 * 
	 * @param name
	 *            Name of the configuration to search for in the map
	 * @return
	 */
	public static HierarchicalConfiguration getConfiguration(String name) {
		return configurations.get(name);
	}

	/**
	 * Returns general configuration.
	 * 
	 * @return
	 * @see #init()
	 */
	public static HierarchicalConfiguration getGeneralConfiguration() {
		return generalConfig;
	}

}
