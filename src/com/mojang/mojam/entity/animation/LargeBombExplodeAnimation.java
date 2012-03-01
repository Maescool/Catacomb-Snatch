package com.mojang.mojam.entity.animation;

import com.mojang.mojam.entity.Entity;
import com.mojang.mojam.level.tile.FloorTile;
import com.mojang.mojam.level.tile.Tile;
import com.mojang.mojam.network.TurnSynchronizer;
import com.mojang.mojam.screen.Screen;

public class LargeBombExplodeAnimation extends Animation {
	public LargeBombExplodeAnimation(double x, double y) {
		super(x, y, TurnSynchronizer.synchedRandom.nextInt(10) + 30); // @random
	}

	@Override
	public void tick() {
		super.tick();
		double dir = TurnSynchronizer.synchedRandom.nextDouble() * Math.PI * 2;

		int maxRadius = (32 - life * 32 / duration) + 16;

		double dist = TurnSynchronizer.synchedRandom.nextDouble() * maxRadius;

		double x = pos.x + Math.cos(dir) * dist;
		double y = pos.y + Math.sin(dir) * dist;

		if (TurnSynchronizer.synchedRandom.nextInt(duration) <= life)
			level.addEntity(new BombExplodeAnimation(x, y));
		else
			level.addEntity(new BombExplodeAnimationSmall(x, y));

		if (life == 25) {
			for (Entity e : level.getEntities(getBB().grow(4 * 32))) {
				e.bomb(this);
			}
			int xt = (int) (pos.x / Tile.WIDTH);
			int yt = (int) (pos.y / Tile.WIDTH);
			int r = 2;
			for (int yy = yt - r; yy <= yt + r; yy++) {
				for (int xx = xt - r; xx <= xt + r; xx++) {
				    level.getTile(xx, yy).bomb(this);
				}
			}
			// update shadows on FloorTiles after all DestroyableWallTiles have been removed
			for (int yy = yt - r; yy <= yt + r; yy++) {
                for (int xx = xt - r; xx <= xt + r; xx++) {
                    if (level.getTile(xx, yy).getName() == FloorTile.NAME)
                        level.getTile(xx, yy).updateShadows();
                }
            }
		}
	}

	public void render(Screen screen) {
	}
}
