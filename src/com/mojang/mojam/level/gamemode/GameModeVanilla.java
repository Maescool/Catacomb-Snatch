package com.mojang.mojam.level.gamemode;

import java.util.Random;

import com.mojang.mojam.entity.building.ShopItem;
import com.mojang.mojam.entity.building.SpawnerEntity;
import com.mojang.mojam.entity.building.TreasurePile;
import com.mojang.mojam.entity.mob.Team;
import com.mojang.mojam.level.tile.FloorTile;
import com.mojang.mojam.level.tile.SandTile;
import com.mojang.mojam.level.tile.Tile;
import com.mojang.mojam.level.tile.UnbreakableRailTile;
import com.mojang.mojam.network.TurnSynchronizer;

public class GameModeVanilla extends GameMode {

	@Override
	protected void setupPlayerSpawnArea() {
		super.setupPlayerSpawnArea();
		
		newLevel.addEntity(new ShopItem(32 * (newLevel.width / 2 - 1.5), 4.5 * 32,
				ShopItem.SHOP_TURRET, Team.Team2));
		newLevel.addEntity(new ShopItem(32 * (newLevel.width / 2 - .5), 4.5 * 32,
				ShopItem.SHOP_HARVESTER, Team.Team2));
		newLevel.addEntity(new ShopItem(32 * (newLevel.width / 2 + .5), 4.5 * 32,
				ShopItem.SHOP_BOMB, Team.Team2));

		newLevel.addEntity(new ShopItem(32 * (newLevel.width / 2 - 1.5), (newLevel.height - 4.5) * 32,
				ShopItem.SHOP_TURRET, Team.Team1));
		newLevel.addEntity(new ShopItem(32 * (newLevel.width / 2 - .5), (newLevel.height - 4.5) * 32,
				ShopItem.SHOP_HARVESTER, Team.Team1));
		newLevel.addEntity(new ShopItem(32 * (newLevel.width / 2 + .5), (newLevel.height - 4.5) * 32,
				ShopItem.SHOP_BOMB, Team.Team1));
		
		newLevel.setTile(31, 7, new UnbreakableRailTile(new SandTile()));
		newLevel.setTile(31, 63 - 7, new UnbreakableRailTile(new SandTile()));
		
		Random random = TurnSynchronizer.synchedRandom;		
		for (int i = 0; i < 11; i++) {
			double x = (random.nextInt(newLevel.width - 16) + 8) * Tile.WIDTH
					+ Tile.WIDTH / 2;
			double y = (random.nextInt(newLevel.height - 16) + 8) * Tile.HEIGHT
					+ Tile.HEIGHT / 2 - 4;
			final Tile tile = newLevel.getTile((int) (x / Tile.WIDTH),
					(int) (y / Tile.HEIGHT));
			if (tile instanceof FloorTile) {
				newLevel.addEntity(new SpawnerEntity(x, y, Team.Neutral, 0));
			}
		}
	}
	
	@Override
	protected void loadColorTile(int color, int x, int y) {
		super.loadColorTile(color, x, y);
		
		if (color == 0xffff00) {
			TreasurePile t = new TreasurePile(x * Tile.WIDTH + 16, y
					* Tile.HEIGHT, Team.Neutral);
			newLevel.setTile(x, y, new FloorTile());
			newLevel.addEntity(t);
		}
	}
	
	@Override
	protected void setTickItems() {
		newLevel.tickItems.add(new RandomSpawner());
	}
	
	@Override
	protected void setVictoryCondition() {
		newLevel.victoryConditions = new FullTreasury();
	}
	
	@Override
	protected void setTargetScore() {
		newLevel.TARGET_SCORE = 100;
	}
}
