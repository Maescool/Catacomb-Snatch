package com.mojang.mojam.resources;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public class Constants {

	protected final Properties constants;

	public Constants() {

		InputStream stream;
		constants = new Properties();

		try {
			stream = this.getClass().getResourceAsStream("/constants/constants.txt");
			constants.load(new InputStreamReader(stream, "UTF8"));
			stream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getString(String constant) {
		if (constants != null && constants.containsKey(constant)) {
			return constants.getProperty(constant);
		} else {
			return "{"+constant+"}";
		}
	}

	public int getInt(String constant) {
		if (constants != null && constants.containsKey(constant)) {
			return Integer.valueOf(constants.getProperty(constant));
		}
		// TODO error handling
		return 0;
	}
}
