package com.mojang.mojam;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Properties;

public class Options {
	
	private static Properties properties = new Properties();;
	
	public static String get(String key) {
		return properties.getProperty(key);
	}
	
	public static void set(String key, String value) {
		properties.setProperty(key, value);
	}

	public static void loadProperties() {
		BufferedInputStream stream;
		try {
			stream = new BufferedInputStream(new FileInputStream(getJarPath() + "options.properties"));
			properties.load(stream);
			stream.close();
		} catch (FileNotFoundException e) {
			// having no properties file is OK
		} catch (IOException e) {
			// something went wrong with the stream
			e.printStackTrace();
		}
	}
	
	public static void saveProperties() {
		BufferedOutputStream stream;
		try {
			File file = new File(getJarPath() + "options.properties");
			if ( !file.exists() ) {
				file.createNewFile();
			}
			stream = new BufferedOutputStream(new FileOutputStream(file));
			// TODO describe properties in comments
			String comments = "";
			properties.store(stream, comments);
		} catch (FileNotFoundException e) {
			// we checked this first so this shouldn't occurs
		} catch (IOException e) {
			// something went wrong with the stream
			e.printStackTrace();
		}
	}
	
	/**
	 * Get the path the game is executed in
	 * @return the absolute path of the jar
	 */
	public static String getJarPath() {
		String path = MojamComponent.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		String decodedPath = "";
		try {
			decodedPath = URLDecoder.decode(path, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return decodedPath.substring(0, path.lastIndexOf("/") + 1);
	}

}
