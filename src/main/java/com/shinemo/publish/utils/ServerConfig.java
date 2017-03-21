package com.shinemo.publish.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.util.Properties;

public class ServerConfig {

	private static final Logger LOG = LoggerFactory.getLogger(ServerConfig.class);

	private static Properties prop = new Properties();
	
	static {
		// load default property file
		loadPropertiesFile("/server.properties");
	}

	private static void loadPropertiesFile(String file) {
		BufferedReader reader = null;
		try {

			URL uri = ServerConfig.class.getResource(file);
			String path = uri.getPath();
			File fileName = new File(path);
			FileInputStream input = new FileInputStream(fileName);
			reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));

			prop.load(reader);
		} catch (Exception e) {
			LOG.error("Failed to load config from " + file
					+ ", cause:" + e.getMessage());
		}
		finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					LOG.error("Failed to close stream from " + file + ", cause:" + e.getMessage());
				}
			}
		}
	}

	/**
	 * Obtains value by specified key
	 * 
	 * @param key
	 * @return might be null if this key is absent
	 */
	public static String get(String key) {
		return prop.getProperty(key);
	}

	/**
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public static String get(String key, String defaultValue) {
		return prop.getProperty(key, defaultValue);
	}

	public static String getMessageByStatus(int status) {
		return prop.getProperty("status." + status + ".msg");
	}

	public static String getErrorMsgInternal(int status) {
		return prop.getProperty("error.msg." + status);
	}

	public static int getPageSize(int size) {
		String sizeStr = get("page.size." + size, "100");
		return Integer.parseInt(sizeStr);
	}



}
