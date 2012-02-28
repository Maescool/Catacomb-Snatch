package com.mojang.mojam.level.gamemode;

import com.mojang.mojam.entity.building.ShopItem;
import com.mojang.mojam.entity.building.SpawnerEntity;
import com.mojang.mojam.entity.mob.Team;
import com.mojang.mojam.level.gamemode.events.Event;
import com.mojang.mojam.level.gamemode.events.EventEntitySpawner;
import com.mojang.mojam.level.gamemode.events.EventTileSwitcher;
import com.mojang.mojam.level.gamemode.events.EventTileTrigger;
import com.mojang.mojam.level.gamemode.events.EventTriggerCreateNewTestEvent;
import com.mojang.mojam.level.tile.DestroyableWallTile;
import com.mojang.mojam.level.tile.SandTile;
import com.mojang.mojam.level.tile.Tile;
import com.mojang.mojam.level.tile.UnbreakableRailTile;
import com.mojang.mojam.math.Vec2;

public class GameModeEventsTest extends GameMode {

	@Override
	protected void setupPlayerSpawnArea() {
		super.setupPlayerSpawnArea();
		
		newLevel.setTile(31, 7, new UnbreakableRailTile(new SandTile()));
		newLevel.setTile(31, 63 - 7, new UnbreakableRailTile(new SandTile()));
	}
	
	@Override
	protected void setTickItems() {
		Event event = new Event(newLevel);
		EventTileTrigger trigger = new EventTileTrigger(false);
		trigger.addTile(new Vec2(28, 54));
		trigger.addTile(new Vec2(28, 53));
		trigger.addTile(new Vec2(29, 53));
		trigger.addTile(new Vec2(30, 53));
		trigger.addTile(new Vec2(31, 53));
		trigger.addTile(new Vec2(32, 53));
		trigger.addTile(new Vec2(33, 53));
		trigger.addTile(new Vec2(34, 53));
		trigger.addTile(new Vec2(34, 54));
		EventTileSwitcher effect = new EventTileSwitcher();
		effect.addTile(new Vec2(29, 55), new DestroyableWallTile());
		effect.addTile(new Vec2(30, 55), new DestroyableWallTile());
		effect.addTile(new Vec2(31, 55), new DestroyableWallTile());
		effect.addTile(new Vec2(32, 55), new DestroyableWallTile());
		effect.addTile(new Vec2(33, 55), new DestroyableWallTile());
		EventEntitySpawner effectspawn = new EventEntitySpawner();
		effectspawn.addEntity(new SpawnerEntity(Tile.WIDTH * 25, Tile.HEIGHT * 53, Team.Neutral, 2));
		effectspawn.addEntity(new SpawnerEntity(Tile.WIDTH * 36, Tile.HEIGHT * 53, Team.Neutral, 2));
		effectspawn.addEntity(new ShopItem(Tile.WIDTH * 30, Tile.HEIGHT * 52, ShopItem.SHOP_BOMB, Team.Team1, Team.Team1));		
		event.add(trigger);
		event.add(effect);
		event.add(effectspawn);
		EventTriggerCreateNewTestEvent testevent = new EventTriggerCreateNewTestEvent();
		event.add(testevent);

		newLevel.addEvent(event);
	}
	
	@Override
	protected void setVictoryCondition() {
	}
	
	@Override
	protected void setTargetScore() {
	}
}