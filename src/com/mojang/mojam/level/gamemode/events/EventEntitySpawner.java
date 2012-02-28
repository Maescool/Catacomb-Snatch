package com.mojang.mojam.level.gamemode.events;

import java.util.ArrayList;

import com.mojang.mojam.entity.Entity;
import com.mojang.mojam.level.Level;

public class EventEntitySpawner implements IEventEffect {

	private ArrayList<Entity> entities;
	
	public EventEntitySpawner() {
		entities = new ArrayList<Entity>();
	}
	
	@Override
	public void triggerEffect(Level level) {
		for(Entity entity : entities) {
			level.addEntity(entity);
		}
	}
	
	public void addEntity(Entity entity) {
		entities.add(entity);
	}

}
