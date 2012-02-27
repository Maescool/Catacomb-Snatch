package com.mojang.mojam.campaign;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.mojang.mojam.MojamComponent;

public class LevelStats {
	// Local variables
	private ArrayList<LevelStatsEntity> allEntity = new ArrayList<LevelStatsEntity>();
	private String levelStatsFile = MojamComponent.getMojamDir() + "/player_level.stats";
	
	// Return a LevelStatsEntity
	public LevelStatsEntity getEntityFromID(int id){
		for(int i = 0; i < allEntity.size(); i++){
			if(id == allEntity.get(i).id){
				return allEntity.get(i);
			}
		}
		
		return null;
	}
	
	
	// Return an ArrayList of entities
	public ArrayList<LevelStatsEntity> getAllEntityFromType(int type){
		ArrayList<LevelStatsEntity> back = new ArrayList<LevelStatsEntity>();
		
		for(int i = 0; i < allEntity.size(); i++){
			if(type == allEntity.get(i).type){
				back.add(allEntity.get(i));
			}
		}
		
		if(back.size() > 0)
			return back;
		else
			return null;
	}
	
	// Check the sum of the entity
	public boolean checkEntitySum(int[] info, int sum){
		int num = 0;
		
		for(int i = 0; i < info.length; i++) 
			num = (num + info[i]) / 2;
		
		if(num == sum)
			return true;
		else
			return false;
	}
	
	// Add an entity to the array
	public void addEntity(int id, int type, int info[]){
		
	}
	
	// Load all entity information
	public void loadAllEntity(){
		BufferedInputStream stream;
		try {
			stream = new BufferedInputStream(new FileInputStream(levelStatsFile));
			// TODO: Add the code to load stats
			
			// Integer.parseInt(hex, 16);
			stream.close();
		} catch (FileNotFoundException e) {
			// No file!!!
		} catch (IOException e) {
			// something went wrong with the stream
			e.printStackTrace();
		}
	}
	
	// Save all the stats!
	public void saveAllEntity(){
		String data = "";
		
		for(int a = 0; a < allEntity.size(); a++){
			// Print the type
			String toAdd = Integer.toHexString(allEntity.get(a).type).toUpperCase();
			if(toAdd.length() % 2 != 0) data += "0" + toAdd;
			data += ":";
			
			// Print the data
			for(int i = 0; i < allEntity.get(a).info.length; i ++){
				toAdd = Integer.toHexString(allEntity.get(a).info[i]).toUpperCase();
				if(toAdd.length() % 2 != 0) data += "0" + toAdd;
				data += ",";
			}
			
			// Add sum
			toAdd = Integer.toHexString(allEntity.get(a).checksum()).toUpperCase();
			if(toAdd.length() % 2 != 0) data += "0" + toAdd;
			
			// Add newline
			data += "\n";
		}
		
		// Print to file
		BufferedOutputStream stream;
		try {
			File file = new File(levelStatsFile);
			if ( !file.exists() ) {
				System.out.println("File not there");
				file.createNewFile();
			}
			stream = new BufferedOutputStream(new FileOutputStream(file));
			
			// TODO: Add the printing to file
			
		} catch (FileNotFoundException e) {
			// we checked this first so this shouldn't occurs
		} catch (IOException e) {
			// something went wrong with the stream
			e.printStackTrace();
		}
	}
	
	// LevelStats Entity
	public class LevelStatsEntity{
		public int id;
		public int type;
		public int[] info;
		
		public LevelStatsEntity(int id, int type, int[] info){
			this.id = id;
			this.type = type;
			this.info = info;
		}
		
		public int checksum(){
			int num = 0;
			
			for(int i = 0; i < info.length; i++) 
				num = (num + info[i]) / 2;
			
			return num;
		}
	}
}
