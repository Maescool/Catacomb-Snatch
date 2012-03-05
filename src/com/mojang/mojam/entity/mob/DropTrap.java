package com.mojang.mojam.entity.mob;

import com.mojang.mojam.entity.Entity;
import com.mojang.mojam.entity.Player;
import com.mojang.mojam.level.IEditable;
//import com.mojang.mojam.level.tile.EmptySpace;
import com.mojang.mojam.level.tile.HoleTile;
import com.mojang.mojam.level.tile.Tile;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Bitmap;
import com.mojang.mojam.screen.Screen;
import java.util.Set;

public class DropTrap extends Mob implements IEditable {
    public static final int COLOR = 0xff0000cc;
	private int crumble;
	private boolean steppedOn;
	private boolean playerFound;

	public DropTrap(double x, double y) {
		super(x, y, Team.Neutral);
		setStartHealth(20);
		this.crumble = 0;
		this.steppedOn = false;
		this.isImmortal = true;
		this.isBlocking = false;
		this.playerFound = false;
		this.yOffs = 0;
	}
	
	public void tick() {
		super.tick();
		if (freezeTime > 0) {
			return;
		}
		freezeTime = 10;
		if (!this.shouldFall()) {
			Set<Entity> entities = level.getEntities(pos.x - 2, pos.y - 2, pos.x + Tile.WIDTH, pos.y + Tile.HEIGHT);
			playerFound = false;
			for (Entity e : entities) {
				if (e instanceof Mob && e instanceof Player) {
						playerFound = true;
						if (!steppedOn) {
							crumble++;
							steppedOn = true;
						}
				}
			}
		} else {
			if (crumble == 3) {
				level.setTile((int)(pos.x/32), (int)(pos.y/32), new HoleTile());
			}
			if (crumble < 7) {
				crumble++;
				freezeTime = 5;
			} else {
				level.removeEntity(this);
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
	
	@Override
	public void render(Screen screen) {
		if (level.getTile((int)(pos.x/32), (int)(pos.y/32)-1) instanceof HoleTile) {
			screen.blit(Art.dropFloor[crumble][0], pos.x, pos.y);
		} else {
			screen.blit(Art.dropFloor[crumble][1], pos.x, pos.y);
		}
	}
    
	@Override
	public Bitmap getSprite() {
		return Art.floorTiles[4][2];
	}

	public boolean isBuildable() {
		return false;
	}

	public boolean isHighlightable() {
		return false;
	}

	@Override
	public int getColor() {
		return DropTrap.COLOR;
	}

	@Override
	public int getMiniMapColor() {
		return DropTrap.COLOR;
	}

	@Override
	public String getName() {
		return "DROP";
	}

	@Override
	public Bitmap getBitMapForEditor() {
		return Art.dropFloor[3][0];
	}
}
