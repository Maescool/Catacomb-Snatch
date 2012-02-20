package com.mojang.mojam.level;

import java.util.ArrayList;

public class LevelList {
	
	private static ArrayList<LevelInformation> levels;
	
	private static void createLevelList() {
		levels = new ArrayList<LevelInformation>();
		levels.add(new LevelInformation("Mojam", "/levels/level1.bmp"));
		levels.add(new LevelInformation("AsymeTrical", "/levels/AsymeTrical.bmp"));		
		levels.add(new LevelInformation("CataBOMB", "/levels/CataBOMB.bmp"));
		levels.add(new LevelInformation("Horizontal8", "/levels/Horizontal8.bmp"));
		levels.add(new LevelInformation("level2", "/levels/level2.bmp"));
		levels.add(new LevelInformation("Testmap", "/levels/Testmap.bmp"));
		levels.add(new LevelInformation("The_Maze", "/levels/TheMaze.bmp"));
		levels.add(new LevelInformation("Circular_Shapes", "/levels/Circular Shapes.bmp"));
		levels.add(new LevelInformation("BlackHole", "/levels/BlackHole.bmp"));
	}
	
	public static ArrayList<LevelInformation> getLevels() {
		if (levels == null) {
			createLevelList();
		}
		return levels;
	}
}
