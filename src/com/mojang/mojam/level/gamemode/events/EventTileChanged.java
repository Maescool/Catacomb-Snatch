package com.mojang.mojam.level.gamemode.events;

import java.util.HashMap;
import java.util.Map;

import com.mojang.mojam.level.Level;
import com.mojang.mojam.level.tile.Tile;
import com.mojang.mojam.math.Vec2;

public class EventTileChanged implements IEventTrigger {

	private boolean bActivated;
	private boolean bAllTilesNeedChanged;
	HashMap<Class<?>, Vec2> tiles;
	
	public EventTileChanged(boolean bAllTilesNeedChanged) {
		bActivated = false;
		this.bAllTilesNeedChanged = bAllTilesNeedChanged;
		tiles = new HashMap<Class<?>, Vec2>();
	}
	
	@Override
	public void updateTrigger(Level level) {
		int triggercount = 0;
		
		for (Map.Entry<Class<?>, Vec2> entry : tiles.entrySet()) {
			Class<?> key = entry.getKey();
		    Vec2 pos = entry.getValue();	
		    
		    if(level.getTile(toPixel(pos)).getClass() != key) {		    	
		    	if(bAllTilesNeedChanged) {
		    		triggercount++;
		    	} else {
		    		bActivated = true;
		    		return;
		    	}
		    }
		}
		
		if(triggercount == tiles.size()) {
			bActivated = true;
		}
	}

	@Override
	public boolean triggerActivated() {
		return bActivated;
	}
	
	public void addTile(Level level, Vec2 position) {
		tiles.put(level.getTile(toPixel(position)).getClass(), position);
	}
	
	private Vec2 toPixel(Vec2 position) {
		return new Vec2(position.x * Tile.WIDTH, position.y * Tile.HEIGHT);
	}

}
