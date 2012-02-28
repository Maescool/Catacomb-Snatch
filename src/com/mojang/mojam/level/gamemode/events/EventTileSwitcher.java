package com.mojang.mojam.level.gamemode.events;

import java.util.HashMap;
import java.util.Map;

import com.mojang.mojam.level.Level;
import com.mojang.mojam.level.tile.Tile;
import com.mojang.mojam.math.Vec2;

public class EventTileSwitcher implements IEventEffect {

	HashMap<Vec2, Tile> tiles;
	
	public EventTileSwitcher() {
		tiles = new HashMap<Vec2, Tile>();
	}
	
	public void addTile(Vec2 position, Tile tile) {
		tiles.put(position, tile);
	}
	
	@Override
	public void triggerEffect(Level level) {
		for (Map.Entry<Vec2, Tile> entry : tiles.entrySet()) {
		    Vec2 key = entry.getKey();
		    Tile value = entry.getValue();		    
		    level.setTile((int)key.x, (int)key.y, value);
		}
	}

}
