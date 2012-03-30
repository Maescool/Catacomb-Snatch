package com.mojang.mojam.level.gamemode;

import java.io.IOException;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.entity.Entity;
import com.mojang.mojam.gui.TitleMenu;
import com.mojang.mojam.level.Level;
import com.mojang.mojam.level.LevelInformation;
import com.mojang.mojam.level.LevelUtils;
import com.mojang.mojam.level.tile.Tile;
import com.mojang.mojam.tiled.TiledMap;

public class GameMode {
	
	protected Level newLevel;
	private TiledMap map;
	
	public Level generateLevel(LevelInformation li)  throws IOException {
		int w;
		int h;
		int layers;
		
		try {
			map = new TiledMap(MojamComponent.class.getResourceAsStream(li.getPath()));
			layers = map.getLayerCount();
			if(layers < 1) {
				throw new IOException("The Map has no layers");
			}
			w = map.getWidth();
			h = map.getHeight();
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			map = null;
			w = 0;
			h = 0;
			layers = 0;
		}
		
		newLevel = new Level(w, h);		
		
		processLevelMap(w, h, layers);
		
		setupPlayerSpawnArea();
		setTickItems();
		setVictoryCondition();
		setTargetScore();
		return newLevel;
	}
	
	private void processLevelMap(int w, int h, int layers) {
		for (int l = 0; l < layers; l++) {			
				processMapLayer(w, h, l);
		}
	}
	
	private void processMapLayer(int w, int h, int mapLayer) {
		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				int id = this.map.getTileId(x, y, mapLayer);
				Object obj = LevelUtils.getNewObjectFromId(x, y, id);
				
				if (obj != null) {
					if (obj instanceof Tile) {
						newLevel.setTile(x, y, (Tile)obj);
					} else if (obj instanceof Entity) {
						newLevel.addEntity((Entity)obj);
					} else if ((int)obj == 32) {
						//TODO Clean up this if statement
						newLevel.getSeen()[x + y * (w + 1)] = true;
					}
				}
			}
		}
	}
	
	protected void setupPlayerSpawnArea() {
		newLevel.maxMonsters = 1500 + (int)TitleMenu.difficulty.calculateStrength(500);
	}
	
	protected void setTickItems() {
	}
	
	protected void setVictoryCondition() {
	}
	
	protected void setTargetScore() {
	}
}