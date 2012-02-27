package com.mojang.mojam.campaign;

import java.util.ArrayList;

public class Level {
	private ArrayList<LevelObjective> allObjectives = new ArrayList<LevelObjective>();
	private LevelStats levelStats;
	
	private boolean playing;
	private boolean nextLevel;
	private int WIDTH, HEIGHT;
	private int levelNum;
	
	public Level(int width, int height){
		this.WIDTH = width;
		this.HEIGHT = height;
		this.playing = true;
	}
	
	public void tick(){
		// Entities
		
		
		// Objectives
		for(int i = 0; i < allObjectives.size(); i++)
			allObjectives.get(i).tick();
		checkObjectives();
		
		// Completed level
		if(!playing && !nextLevel){
			// TODO the player ended the game
		}
		else if(!playing && nextLevel){
			// TODO the player moves to next level
		} 
	}
	
	private void checkObjectives(){
		boolean completed = false;
		
		for(int i = 0; i < allObjectives.size(); i++){
			completed &= allObjectives.get(i).isCompleted();
		}
		
		if(completed){
			playing = false;
			nextLevel = true;
		}
	}
}
