package com.mojang.mojam.level.gamemode;

import java.util.Random;

import com.mojang.mojam.entity.building.SpawnerEntity;
import com.mojang.mojam.level.Level;
import com.mojang.mojam.level.tile.FloorTile;
import com.mojang.mojam.level.tile.Tile;
import com.mojang.mojam.network.TurnSynchronizer;

public class RandomSpawner implements ILevelTickItem {

	@Override
	public void tick(Level level) {
		final Random random = TurnSynchronizer.synchedRandom;
		final int width = level.width;
		final int height = level.height;

		final double x = (random.nextInt(width - 16) + 8) * Tile.WIDTH
				+ Tile.WIDTH / 2;
		final double y = (random.nextInt(height - 16) + 8) * Tile.HEIGHT
				+ Tile.HEIGHT / 2 - 4;
		final Tile tile = level.getTile((int) (x / Tile.WIDTH),
				(int) (y / Tile.HEIGHT));
		if (tile instanceof FloorTile
				&& SpawnerSurroundingsChecker.isClear(level, x, y)) {
			level.addEntity(SpawnerEntity.getRandomSpawner(x, y));
		}
	}

}
