package com.mojang.mojam.entity.building;

import com.mojang.mojam.entity.Entity;
import com.mojang.mojam.entity.mob.Mob;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Bitmap;

public class TreasurePile extends Building {

	private int treasures = 40;

	public TreasurePile(double x, double y, int team) {
		super(x, y, team);
		setStartHealth(20);
		freezeTime = 10;
		minimapIcon = 5;
		isImmortal = true;
	}

	@Override
	public boolean isNotFriendOf(Mob m) {
		return false;
	}

	public void tick() {
		super.tick();
		if (freezeTime > 0)
			return;
	}

	public Bitmap getSprite() {
		return Art.treasureTiles[0][0];
	}

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
