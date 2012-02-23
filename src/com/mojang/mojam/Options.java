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

	public static final String DRAW_FPS = "drawFps";
	public static final String FULLSCREEN = "fullscreen";
	public static final String MUTE_MUSIC = "muteMusic";

	public static final String KEY_UP = "key_up";
	public static final String KEY_DOWN = "key_down";
	public static final String KEY_LEFT = "key_left";
	public static final String KEY_RIGHT = "key_right";
	public static final String KEY_FIRE = "key_fire";
	public static final String KEY_BUILD = "key_build";
	public static final String KEY_USE = "key_use";
	public static final String KEY_UPGRADE = "key_upgrade";
	public static final String KEY_SPRINT = "key_sprint";

	public static final String VALUE_TRUE = "true";
	public static final String VALUE_FALSE = "false";

	private static Properties properties = new Properties();

	public static String get(String key) {
		return properties.getProperty(key);
	}

	public static String get(String key, String defaultValue) {
		return properties.getProperty(key, defaultValue);
	}

	public static Boolean getAsBoolean(String key) {
		return Boolean.parseBoolean(get(key));
	}

	public static Boolean getAsBoolean(String key, String defaultValue) {
		return Boolean.parseBoolean(get(key, defaultValue));
	}

	public static Integer getAsInteger(String key) {
		try {
			return Integer.parseInt(get(key));
		} catch (NumberFormatException e) {
			return null;
		}
	}

	public static Integer getAsInteger(String key, Integer defaultValue) {
		String property = get(key);
		if (property != null) {
			try {
				return Integer.parseInt(property);
			} catch (NumberFormatException e) {
				return defaultValue;
			}
		} else {
			return defaultValue;
		}
	}

	public static void set(String key, String value) {
		properties.setProperty(key, value);
	}

	public static void set(String key, boolean value) {
		properties.setProperty(key, String.valueOf(value));
	}

	public static void set(String key, int value) {
		properties.setProperty(key, String.valueOf(value));
	}

	public static void loadProperties() {
		BufferedInputStream stream;
		try {
			stream = new BufferedInputStream(new FileInputStream(MojamComponent.getMojamDir()
					+ "/options.properties"));
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
			File file = new File(MojamComponent.getMojamDir() + "/options.properties");
			if (!file.exists()) {
				System.out.println("File not there");
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
	 * 
	 * @return the absolute path of the jar
	 */
	public static String getJarPath() {
		String path = MojamComponent.class.getProtectionDomain().getCodeSource().getLocation()
				.getPath();
		String decodedPath = "";
		try {
			decodedPath = URLDecoder.decode(path, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return decodedPath.substring(0, decodedPath.lastIndexOf("/") + 1);
	}

}
