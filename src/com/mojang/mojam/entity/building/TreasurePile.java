package com.mojang.mojam.entity.building;

import com.mojang.mojam.entity.Entity;
import com.mojang.mojam.entity.mob.Mob;
import com.mojang.mojam.entity.mob.Team;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Bitmap;

/**
 * Treasure pile. The player has to harvest 50 batches from this using raildroids to win.
 */
public class TreasurePile extends Building {

	private int treasures = 40;

	/**
	 * Constructor
	 * 
	 * @param x Initial X coordinate
	 * @param y Initial Y coordinate
	 * @param team Team number
	 * @param localTeam Local team number
	 */
	public TreasurePile(double x, double y, int team, int localTeam) {
		super(x, y, Team.Neutral,localTeam);
		setStartHealth(20);
		freezeTime = 10;
		minimapIcon = 5;
		isImmortal = true;
	}

	@Override
	public boolean isNotFriendOf(Mob m) {
		return false;
	}

	@Override
	public void tick() {
		super.tick();
		if (freezeTime > 0)
			return;
	}

	@Override
	public Bitmap getSprite() {
		return Art.treasureTiles[0][0];
	}

	/**
	 * Get the amount of remaining treasure
	 * 
	 * @return Remaining treasure
	 */
	public int getRemainingTreasure() {
		return treasures;
	}

	@Override
	public void use(Entity user) {

	}

	@Override
	public boolean isHighlightable() {
		return false;
	}
}
