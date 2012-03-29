package com.mojang.mojam.entity.building;

import com.mojang.mojam.entity.mob.Team;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.AbstractBitmap;
import com.mojang.mojam.screen.AbstractScreen;

/**
 * this is the treasure from the center of the map, RailDroids carry
 * it back to base and the player scores!
 * this is a building simply so the rail droids can carry it
 */

public class CatacombTreasure extends Building {

	/**
	 * Constructor
	 * 
	 * @param x Initial X coordinate
	 * @param y Initial Y coordinate
	 */
	public CatacombTreasure(double x, double y) {
		//always Neutral
		super(x, y, Team.Neutral);
		setStartHealth(20);
		freezeTime=20;
		isImmortal = true;
		yOffs=-8;
	}

	@Override
	public void tick() {
		//do nothing?
		super.tick();
	}

	@Override
	public void render(AbstractScreen screen) {
		super.render(screen);
	}

	@Override
	public AbstractBitmap getSprite() {
		//bullets? really?
		return Art.bullets[0][0];
	}
}
