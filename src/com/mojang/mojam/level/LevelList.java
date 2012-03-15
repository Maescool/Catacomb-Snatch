package com.mojang.mojam.level;

import java.io.File;
import java.util.ArrayList;

import com.mojang.mojam.MojamComponent;

public class LevelList {

	private static ArrayList<LevelInformation> levels;

	public static void createLevelList() {
		levels = new ArrayList<LevelInformation>();
		levels.add(new LevelInformation("Dev TMX", "/levels/DEV.tmx",true));
		
		File levels = getBaseDir();
		if(!levels.exists()) levels.mkdirs();
		System.out.println("Looking for levels: "+levels.getPath());
		loadDir(levels);
	}
	
	public static File getBaseDir(){
		return new File(MojamComponent.getMojamDir(), "levels");
	}
	
	public static void loadDir(File file){
		File[] children = file.listFiles();
	    if (children != null) {
	        for (File child : children) {
	            if(child.isDirectory()){
	            	loadDir(child);
	            	continue;
	            }
	            String fileName = child.getName();
	            String fname="";
	            String ext="";
	            int mid= fileName.lastIndexOf(".");
	            fname=fileName.substring(0,mid);
	            ext=fileName.substring(mid+1);
	            if(ext.toLowerCase().equals("tmx")){
	            	System.out.println("  Found level: "+fname+" . "+ext);
	        		levels.add(new LevelInformation("+ "+fname, child.getPath(),false));
	            }
	        }
	    }
	}

	public static ArrayList<LevelInformation> getLevels() {
		if (levels == null) {
			createLevelList();
		}
		return levels;
	}
	
	public static void resetLevels(){
		levels = null;
	}
}
