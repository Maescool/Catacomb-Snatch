package com.mojang.mojam.level.tile;

import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Bitmap;

public class PlayerRailTile extends RailTile {
	public static final int COLOR = 0xff969696;
	private final int team;
	
	
	public PlayerRailTile(int team) {
		super();
		this.team = team;
	}

	public boolean remove() {
		return false;
	}
	
	public int getColor() {
		return PlayerRailTile.COLOR;
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
	
	public boolean isTeam(int team) {
		return this.team == team;
	}
}
