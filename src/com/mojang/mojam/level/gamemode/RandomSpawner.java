package com.mojang.mojam.level.gamemode;

import java.util.Random;

import com.mojang.mojam.entity.Player;
import com.mojang.mojam.entity.building.SpawnerEntity;
import com.mojang.mojam.entity.building.Turret;
import com.mojang.mojam.level.Level;
import com.mojang.mojam.level.tile.FloorTile;
import com.mojang.mojam.level.tile.Tile;
import com.mojang.mojam.math.BB;
import com.mojang.mojam.network.TurnSynchronizer;

public class RandomSpawner implements ILevelTickItem {

	@Override
	public void tick(Level level) {
		Random random = TurnSynchronizer.synchedRandom;
		int width = level.width;
		int height = level.height;

		double x = (random.nextInt(width - 16) + 8) * Tile.WIDTH
				+ Tile.WIDTH / 2;
		double y = (random.nextInt(height - 16) + 8) * Tile.HEIGHT
				+ Tile.HEIGHT / 2 - 4;
		final Tile tile = level.getTile((int) (x / Tile.WIDTH),
				(int) (y / Tile.HEIGHT));
		if (tile instanceof FloorTile) {
			double r = 32 * 8;
			if (level.getEntities(new BB(null, x - r, y - r, x + r, y + r),
					Player.class).size() == 0) {
				r = 32 * 8;
				if (level.getEntities(new BB(null, x - r, y - r, x + r, y + r),
						SpawnerEntity.class).size() == 0) {
					r = 32 * 4;
					if (level.getEntities(
							new BB(null, x - r, y - r, x + r, y + r),
							Turret.class).size() == 0) {
						level.addEntity(SpawnerEntity.getRandomSpawner(x, y));
					}
				}
			}
		}
	}

}
