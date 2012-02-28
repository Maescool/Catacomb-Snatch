package com.mojang.mojam.level.gamemode.events;

import java.util.ArrayList;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.entity.Player;
import com.mojang.mojam.level.Level;
import com.mojang.mojam.level.tile.Tile;
import com.mojang.mojam.math.Vec2;

public class EventTileTrigger implements IEventTrigger {

	private boolean bActivated;
	private boolean bAllPlayers;
	private ArrayList<Vec2> triggerTiles;
	
	private int timesTriggered;
	
	public EventTileTrigger(boolean bAllPlayers) {
		bActivated = false;
		triggerTiles = new ArrayList<Vec2>();
		this.bAllPlayers = bAllPlayers;
	}
	
	@Override
	public void updateTrigger(Level level) {
		timesTriggered = 0;
		for (int i = 0; i < MojamComponent.instance.players.length; i++) {
			if(MojamComponent.instance.players[i] != null)
				checkPlayerPosition(MojamComponent.instance.players[i]);
		}
		
		if(bAllPlayers) {
			if(timesTriggered == MojamComponent.instance.players.length) {
				bActivated = true;
			}
		} else if (timesTriggered > 0){
			bActivated = true;
		}
	}

	private void checkPlayerPosition(Player player) {
		Vec2 tileposition = new Vec2((int)(player.getPosition().x / Tile.WIDTH), (int)(player.getPosition().y / Tile.HEIGHT));
		for(Vec2 tile : triggerTiles) {			
			if(tileposition.x == tile.x && tileposition.y == tile.y) {
				timesTriggered++;
				return;
			}
		}
	}

	@Override
	public boolean triggerActivated() {
		return bActivated;
	}
	
	public void addTile(Vec2 tileposition) {
		triggerTiles.add(tileposition);
	}

}
