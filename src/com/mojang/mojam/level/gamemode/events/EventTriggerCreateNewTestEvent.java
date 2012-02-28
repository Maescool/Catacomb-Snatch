package com.mojang.mojam.level.gamemode.events;

import com.mojang.mojam.entity.building.SpawnerEntity;
import com.mojang.mojam.entity.mob.Team;
import com.mojang.mojam.level.Level;
import com.mojang.mojam.level.tile.Tile;
import com.mojang.mojam.math.Vec2;

public class EventTriggerCreateNewTestEvent implements IEventEffect {

	@Override
	public void triggerEffect(Level level) {
		Event event = new Event(level);
		EventTileChanged trigger = new EventTileChanged(false);
		trigger.addTile(level, new Vec2(29, 55));		
		trigger.addTile(level, new Vec2(30, 55));
		trigger.addTile(level, new Vec2(31, 55));
		trigger.addTile(level, new Vec2(32, 55));
		trigger.addTile(level, new Vec2(33, 55));
		EventEntitySpawner effectspawn = new EventEntitySpawner();
		effectspawn.addEntity(new SpawnerEntity(Tile.WIDTH * 27, Tile.HEIGHT * 52, Team.Neutral, 2));
		effectspawn.addEntity(new SpawnerEntity(Tile.WIDTH * 34, Tile.HEIGHT * 52, Team.Neutral, 2));
		event.add(trigger);
		event.add(effectspawn);
		
		level.addEvent(event);
	}

}
