package com.mojang.mojam.entity.mob;

import com.mojang.mojam.entity.Entity;
import com.mojang.mojam.level.DifficultyInformation;
import com.mojang.mojam.level.IEditable;
import com.mojang.mojam.level.tile.Tile;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Bitmap;
import com.mojang.mojam.screen.Screen;
import java.util.Set;

public class SpikeTrap extends Mob implements IEditable {
    public static final int COLOR = 0xff0000ff;
	private int spike = 0;

	public SpikeTrap(double x, double y) {
		super(x, y, Team.Neutral);
		setStartHealth(20);
		this.isImmortal = true;
		this.isBlocking = false;
		this.yOffs = 0;
	}

	@Override
	public void tick() {
		super.tick();
		if (freezeTime > 0) {
			return;
		}
		freezeTime = 10;
		if (this.isSpiking()) {
			Set<Entity> entities = level.getEntities(pos.x - 2, pos.y - 2, pos.x + Tile.WIDTH, pos.y + Tile.HEIGHT);
			for (Entity e : entities) {
				if (e instanceof Mob && !(e instanceof Bat)) {
					((Mob) e).hurt(this, DifficultyInformation.calculateStrength(1));
				}
			}
		}
		if (spike == 3) {
			spike = 0;
			freezeTime = 100;
		} else {
			spike++;
		}
	}
    
	@Override
	public void render(Screen screen) {
		screen.blit(Art.spikes[spike][0], pos.x, pos.y);
	}
    
	@Override
	public Bitmap getSprite() {
		return Art.floorTiles[4][2];
	}

	public boolean isSpiking() {
		if (spike == 1) {
			return true;
		}
		return false;
	}

	public boolean isBuildable() {
		return false;
	}

	public boolean isHighlightable() {
		return false;
	}

	@Override
	public int getColor() {
		return SpikeTrap.COLOR;
	}

	@Override
	public int getMiniMapColor() {
		return SpikeTrap.COLOR;
	}

	@Override
	public String getName() {
		return "SPIKES";
	}

	@Override
	public Bitmap getBitMapForEditor() {
		return Art.spikes[1][0];
	}
}
