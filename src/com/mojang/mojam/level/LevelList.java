package com.mojang.mojam.level;

import java.io.File;
import java.util.ArrayList;

import com.mojang.mojam.MojamComponent;

public class LevelList {

	private static ArrayList<LevelInformation> levels;

	public static void createLevelList() {
		levels = new ArrayList<LevelInformation>();
		levels.add(new LevelInformation("Mojam", "/levels/level1.bmp",true));
		levels.add(new LevelInformation("AsymeTrical","/levels/AsymeTrical.bmp",true));
		levels.add(new LevelInformation("CataBOMB", "/levels/CataBOMB.bmp",true));
		levels.add(new LevelInformation("Siege","/levels/Siege.bmp",true));
		levels.add(new LevelInformation("TheMaze", "/levels/TheMaze.bmp",true));
		levels.add(new LevelInformation("Circular_Shapes", "/levels/Circular Shapes.bmp",true));
		levels.add(new LevelInformation("BlackHole", "/levels/BlackHole.bmp",true));
		levels.add(new LevelInformation("Railroads", "/levels/RailRoads.bmp",true));
		
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
	            System.out.println("  Found level: "+fname+" . "+ext);
	            if(ext.toLowerCase().equals("bmp")){
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
