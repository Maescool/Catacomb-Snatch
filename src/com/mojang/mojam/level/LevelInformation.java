package com.mojang.mojam.level;

import java.util.HashMap;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.mc.EnumOS2;

public class LevelInformation {
	public static HashMap<String, LevelInformation> fileToInfo = new HashMap<String, LevelInformation>();
	private static int localIDcounter = 0;

	public static final boolean unix = MojamComponent.getOs().equals(
			EnumOS2.linux)
			|| MojamComponent.getOs().equals(EnumOS2.macos);
	public static final String seperator = unix ? "/" : "\\";

	public int localID;
	public String levelName;
	private String levelFile;
	public String levelAuthor;
	public String levelDescription;
	public boolean vanilla;

	// public LevelInformation(String levelName_, String levelFile_) {
	// this.levelName = levelName_;
	// this.levelFile = sanitizePath(levelFile_);
	// vanilla = isPathVanilla(levelFile);
	//
	// localID = localIDcounter++;
	// fileToInfo.put(levelFile, this);
	// System.out.println("Map info added: "+levelFile+"("+(vanilla?"vanilla":"external")+")");
	// }

	public LevelInformation(String levelName, String levelFile, boolean vanilla) {
		this.levelName = levelName;
		this.levelFile = sanitizePath(levelFile);
		this.vanilla = vanilla;

		localID = localIDcounter++;
		fileToInfo.put(levelFile, this);
		System.out.println("Map info added: " + levelFile + "("
				+ (vanilla ? "vanilla" : "external") + ")");
	}

	public String getPath() {
		if (vanilla)
			return levelFile;
		return MojamComponent.getMojamDir() + seperator + levelFile;
	}

	public String getUniversalPath() {
		return levelFile;
	}

	public static String sanitizePath(String s) {
		if (isPathVanilla(s)) {
			return s;
		}
		return s.substring(s.indexOf("levels"));
	}

	public LevelInformation setAuthor(String s) {
		this.levelAuthor = s;
		return this;
	}

	public LevelInformation setDescription(String s) {
		this.levelDescription = s;
		return this;
	}

	public static boolean isPathVanilla(String s) {
		if (unix) {
			if (s.startsWith("/Users/")) // macos
				return false;
			if (s.startsWith("/home/")) // linux
				return false;
			return true;
		}
		return s.startsWith("/");
	}

	public static LevelInformation getInfoForPath(String s) {
		System.out.println("Path -> info: " + s);
		if (isPathVanilla(s))
			return fileToInfo.get(s);
		return fileToInfo.get(sanitizePath(s));
	}

	public static boolean isMacOS() {
		String osName = System.getProperty("os.name");
		return osName.startsWith("Mac");
	}
}
