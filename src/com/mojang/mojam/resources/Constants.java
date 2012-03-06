package com.mojang.mojam.resources;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public class Constants {

	private final static Properties constants = new Properties();

	public Constants() {

		InputStream stream;

		try {
			stream = this.getClass().getResourceAsStream("/constants/constants.txt");
			constants.load(new InputStreamReader(stream, "UTF8"));
			stream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String getString(String constant) {
		if (constants != null && constants.containsKey(constant)) {
			return constants.getProperty(constant);
		} else {
			return "{" + constant + "}";
		}
	}

	public static int getInt(String constant, Object obj) {
		StringBuffer string = new StringBuffer(obj.getClass().getName());
		string.append(".").append(constant);
		return getInt(string.toString());
	}

	public static int getInt(String constant) {
		if (constants != null && constants.containsKey(constant)) {
			return Integer.valueOf(constants.getProperty(constant));
		}
		// TODO error handling
		System.err.println("no property found for constant: " + constant);
		return 0;
	}

	public static float getFloat(String constant, Object obj) {
		StringBuffer string = new StringBuffer(obj.getClass().getName());
		string.append(".").append(constant);
		return getFloat(string.toString());
	}

	public static float getFloat(String constant) {
		if (constants != null && constants.containsKey(constant)) {
			return Float.valueOf(constants.getProperty(constant));
		}
		// TODO error handling
		System.err.println("no property found for constant: " + constant);
		return 0;
	}
	
	public static double getDouble(String constant, Object obj) {
		StringBuffer string = new StringBuffer(obj.getClass().getName());
		string.append(".").append(constant);
		return getFloat(string.toString());
	}
	
	public static double getDoublet(String constant) {
		if (constants != null && constants.containsKey(constant)) {
			return Double.valueOf(constants.getProperty(constant));
		}
		// TODO error handling
		System.err.println("no property found for constant: " + constant);
		return 0;
	}
}
