package com.mojang.mojam.level;

import java.util.HashMap;

import com.mojang.mojam.MojamComponent;

public class LevelInformation {
	public static HashMap<String, LevelInformation> fileToInfo = new HashMap<String, LevelInformation>();
	private static int localIDcounter = 0;
	
	public static final boolean mac = isMacOS();
	public static final String seperator = mac ? "/" : "\\";
	
	public int localID;
	public String levelName;
	private String levelFile;
	public String levelAuthor;
	public String levelDescription;
	public boolean vanilla;

	public LevelInformation(String levelName_, String levelFile_) {
		this.levelName = levelName_;
		this.levelFile = sanitizePath(levelFile_);
		vanilla = isPathVanilla(levelFile);
		
		localID = localIDcounter++;
		fileToInfo.put(levelFile, this);
		System.out.println("Map info added: "+levelFile);
	}
	
	public String getPath(){
		if(vanilla) return levelFile;
		return MojamComponent.getMojamDir()+seperator+levelFile;
	}
	public String getUniversalPath(){
		return levelFile;
	}
	
	public static String sanitizePath(String s){
		if(isPathVanilla(s)){
			return s;
		}
		return s.substring(s.indexOf("levels"));
	}

	public LevelInformation setAuthor(String s){
		this.levelAuthor = s;
		return this;
	}
	
	public LevelInformation setDescription(String s){
		this.levelDescription = s;
		return this;
	}
	
	public static boolean isPathVanilla(String s){
		if(mac) return !s.startsWith("/Users/");
		return s.startsWith("/");
	}
	
	public static LevelInformation getInfoForPath(String s){
		System.out.println("Path -> info: "+s);
		if(isPathVanilla(s)) return fileToInfo.get(s);
		return fileToInfo.get(sanitizePath(s));
	}
	
	public static boolean isMacOS() {
	    String osName = System.getProperty("os.name");
	    return osName.startsWith("Mac");
	}
}
