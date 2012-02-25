package com.mojang.mojam.entity.building;

import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Bitmap;

public class Base extends Building {
	public Base(double x, double y, int team, int localTeam) {
		super(x, y, team, localTeam);
		setStartHealth(20);
		freezeTime = 10;
	}

	public void tick() {
		super.tick();
		if (freezeTime > 0)
			return;
	}

	@Override
	public Bitmap getSprite() {
		return Art.floorTiles[3][2];
	}

	@Override
	public boolean isHighlightable() {
		return false;
	}
}
