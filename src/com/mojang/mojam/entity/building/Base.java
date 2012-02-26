package com.mojang.mojam.entity.building;

import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Bitmap;

/**
 * Building base class
 */
public class Base extends Building {
	/**
	 * Constructor
	 * 
	 * @param x Initial X coordinate
	 * @param y Initial Y coordinate
	 * @param team Team number
	 * @param localTeam Local team number
	 */
	public Base(double x, double y, int team, int localTeam) {
		super(x, y, team, localTeam);
		
		setStartHealth(20);
		freezeTime = 10;
	}

	@Override
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
