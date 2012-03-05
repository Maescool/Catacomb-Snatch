package com.mojang.mojam.entity.building;

import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Bitmap;
import com.mojang.mojam.screen.Screen;

/**
 * Player base
 */
public class Base extends Building {
	/**
	 * Constructor
	 * 
	 * @param x Initial X coordinate
	 * @param y Initial Y coordinate
	 * @param team Team number
	 */
	public Base(double x, double y, int team) {
		super(x, y, team);
		
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
	public void render(Screen screen) {
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
