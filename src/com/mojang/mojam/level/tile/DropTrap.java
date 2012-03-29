package com.mojang.mojam.level.tile;

import java.util.Set;

import com.mojang.mojam.entity.Entity;
import com.mojang.mojam.entity.Player;
import com.mojang.mojam.level.Level;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.AbstractBitmap;
import com.mojang.mojam.screen.AbstractScreen;

public class DropTrap extends AnimatedTile {

	public static final int COLOR = 0xff0000CC;
	public static final String NAME = "DROP TRAP";
	private int crumble;
	private boolean steppedOn;
	private boolean playerFound;
	public int freezeTime = 0;

	public DropTrap() {
		crumble = 0;
		steppedOn = false;
		playerFound = false;
	}

	@Override
	public void render(AbstractScreen screen) {
		if (level.getTile(x, y-1) instanceof HoleTile) {
			screen.blit(Art.dropFloor[crumble][0], x * Tile.WIDTH, y * Tile.HEIGHT);
		} else {
			screen.blit(Art.dropFloor[crumble][1], x * Tile.WIDTH, y * Tile.HEIGHT);
		}
	}

	@Override
	public int getColor() {
		return DropTrap.COLOR;
	}

	@Override
	public String getName() {
		return DropTrap.NAME;
	}

	@Override
	public AbstractBitmap getBitMapForEditor() {
		return Art.dropFloor[3][0];
	}

	@Override
	public int getMiniMapColor() {
		return minimapColor;
	}

	@Override
	public void tick(Level level) {
		if (freezeTime > 0) {
			freezeTime--;
			return;
		}
		freezeTime = 10;
		if (!shouldFall()) {
			Set<Entity> entities = level.getEntities((x * Tile.WIDTH) - 2, (y * Tile.HEIGHT) - 2, 
					(x * Tile.WIDTH) + Tile.WIDTH, (y * Tile.HEIGHT) + Tile.HEIGHT);
			playerFound = false;
			for (Entity entity : entities) {
				if (entity instanceof Player) {
						playerFound = true;
						if (!steppedOn) {
							crumble++;
							steppedOn = true;
						}
				}
			}
		} else {
			if (crumble == 3) {
				level.setTile(x, y, new HoleTile());
			}
			if (crumble < 7) {
				crumble++;
				freezeTime = 5;
			} 
		}
		
		if (!playerFound) {
			steppedOn = false;
		}
	}
	
	public boolean shouldFall() {
		if (crumble > 2) {
			return true;
		}
		return false;
	}
}
