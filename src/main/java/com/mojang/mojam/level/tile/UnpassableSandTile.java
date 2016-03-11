package com.mojang.mojam.level.tile;

import com.mojang.mojam.entity.Entity;
import com.mojang.mojam.level.Level;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.AbstractBitmap;
import com.mojang.mojam.screen.AbstractScreen;

public class UnpassableSandTile extends Tile {
	public static final int COLOR = 0xff888800;
	private static final String NAME = "UNPASSABLE SAND";

	public UnpassableSandTile() {
		img = 6;
		minimapColor = Art.floorTileColors[img & 7][img / 8];
	}
	
	public void init(Level level, int x, int y) {
		super.init(level, x, y);

	}

	public void render(AbstractScreen screen) {
		super.render(screen);
	}

	public boolean canPass(Entity e) {
		return false;
	}

	public int getColor() {
		return UnpassableSandTile.COLOR;
	}

	public String getName() {
		return UnpassableSandTile.NAME;
	}
	
	@Override
	public AbstractBitmap getBitMapForEditor() {
		return Art.floorTiles[6][0];
	}
	
	@Override
	public int getMiniMapColor() {
		return minimapColor;
	}
}
