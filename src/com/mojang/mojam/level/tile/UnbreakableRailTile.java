package com.mojang.mojam.level.tile;

import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Bitmap;

public class UnbreakableRailTile extends RailTile {
	public static final int COLOR = 0xff969696;

	public UnbreakableRailTile(Tile parent) {
		super(parent);
	}

	public boolean remove() {
		return false;
	}
	
	public int getColor() {
		return UnbreakableRailTile.COLOR;
	}

	public String getName() {
		return RailTile.NAME;
	}

	@Override
	public Bitmap getBitMapForEditor() {
		return Art.rails[1][0];
	}
	
	@Override
	public int getMiniMapColor() {
		return minimapColor;
	}
}
