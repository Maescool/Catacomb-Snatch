package com.mojang.mojam.level;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.level.gamemode.GameMode;
import com.mojang.mojam.mc.EnumOS2;
import com.mojang.mojam.network.TurnSynchronizer;
import com.mojang.mojam.resources.MD5Checksum;
import com.mojang.mojam.screen.Bitmap;

public class LevelInformation {
	public static HashMap<String, LevelInformation> fileToInfo = new HashMap<String, LevelInformation>();
	public static HashMap<String, LevelInformation> md5ToInfo = new HashMap<String, LevelInformation>();
	private static int localIDcounter = 0;
	
	public static final boolean unix = MojamComponent.getOs().equals(EnumOS2.linux)||MojamComponent.getOs().equals(EnumOS2.macos);
	public static final String seperator = unix ? "/" : "\\";
	
	public int localID;
	private String levelName;
	private String levelFile;
	public String levelAuthor = "";
	public String levelDescription = "";
	public boolean vanilla;
	
	private Bitmap minimap;
	private Level parent;
	private String checksum = "";
	
	public LevelInformation(){
		this("BLANK MAP", "", false);
	}
	
	public LevelInformation(String levelName, String levelFile, boolean vanilla) {
		this.levelName = levelName;
		this.levelFile = sanitizePath(levelFile);
		this.vanilla = vanilla;
		
		localID = localIDcounter++;
		fileToInfo.put(levelFile, this);
		
		System.out.println("Map info added: "+levelFile+"("+(vanilla?"vanilla":"external")+")");
	}
	
	public LevelInformation setParent(Level level){
		this.parent = level;
		return this;
	}
	
	public String getChecksum(){
		if(checksum == ""){
			if(!vanilla){
				try {
					checksum = MD5Checksum.getMD5Checksum(getPath());
					md5ToInfo.put(checksum, this);
				} catch (Exception e) {
					e.printStackTrace();
				}
				System.out.println(getPath());
				System.out.println("  MD5:"+checksum);
			}
		}
		return checksum;
	}
	
	public String getPath()
	{ return getPath(vanilla); }
	public String getPath(boolean reqVanilla){
		if(reqVanilla) return levelFile;
		return MojamComponent.getMojamDir()+seperator+levelFile;
	}
	public String getUniversalPath()
	{ return levelFile; }
	
	public String getName()
	{ return (vanilla?"":"+ ")+levelName; }
	public String getNameRaw()
	{ return levelName; }
	
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
		if(unix) {
			if (s.startsWith("/Users/")) // macos
				return false;
			if (s.startsWith("/home/")) // linux
				return false;
			return true;
		}
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
	
	public Bitmap getButtonMinimap(){
		if(minimap == null){
			// back it up and use a local new one instead, just to make sure
			Random backupRandom = TurnSynchronizer.synchedRandom;
			TurnSynchronizer.synchedRandom = new Random();

			// load level
			Level l = parent;
			if(l == null){
				try {
					l = new GameMode().generateLevel(this);
				} catch (IOException e) {
					e.printStackTrace();
					return null;
				}
			}

			int w = l.width;
			int h = l.height;

			minimap = new Bitmap(w, h);

			for (int y = 0; y < h; y++) {
				for (int x = 0; x < w; x++) {
					minimap.pixels[x + y * w] = l.getTile(x, y).minimapColor;
				}
			}

			TurnSynchronizer.synchedRandom = backupRandom;
		}
		return minimap;
	}
	
	public LevelInformation copy(){
		return new LevelInformation(levelName, levelFile, vanilla).setAuthor(levelAuthor).setDescription(levelDescription);
	}
	
	public void sendMP(DataOutputStream dos) throws IOException {
		dos.writeUTF(levelName);
		dos.writeUTF(levelFile);
		dos.writeBoolean(vanilla);
		dos.writeUTF(levelAuthor);
		dos.writeUTF(levelDescription);
	}
	
	public static LevelInformation readMP(DataInputStream dis) throws IOException{
		String levelName = dis.readUTF();
		String levelPath = dis.readUTF();
		System.out.println("INCOMMING: "+levelPath);
		int i = levelPath.lastIndexOf(seperator);
		levelPath = "levels"+seperator+"MP"+seperator+levelPath.substring(i);
		LevelInformation li = new LevelInformation(levelName, levelPath, dis.readBoolean());
		li.setAuthor(dis.readUTF());
		li.setDescription(dis.readUTF());
		return li;
	}
}
