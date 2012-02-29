package com.mojang.mojam.level.tile;

public class UnbreakableRailTile extends RailTile {
	public static final int COLOR = 0x969696;

	public UnbreakableRailTile(Tile parent) {
		super(parent);
	}

	public boolean remove() {
		return false;
	}
}
