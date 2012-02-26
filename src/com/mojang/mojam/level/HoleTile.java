package com.mojang.mojam.level;

import com.mojang.mojam.entity.Bullet;
import com.mojang.mojam.entity.Entity;
import com.mojang.mojam.entity.Player;
import com.mojang.mojam.entity.mob.Bat;
import com.mojang.mojam.level.tile.Tile;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Screen;

public class HoleTile extends Tile {

	public void init(Level level, int x, int y) {
		super.init(level, x, y);
		img = 4;
		minimapColor = Art.floorTileColors[img & 7][img / 8];
	}

	public void render(Screen screen) {
		if (y > 0 && !(level.getTile(x, y - 1) instanceof HoleTile)) {
			super.render(screen);
		} else {
			screen.fill(x * Tile.WIDTH, y * Tile.HEIGHT, Tile.WIDTH,
					Tile.HEIGHT, 0xff000000);
		}
	}

	public boolean isBuildable() {
		return false;
	}

	public boolean canPass(Entity e) {
		return ((e instanceof Bullet) || (e instanceof Bat) || (e instanceof Player));
	}
}
