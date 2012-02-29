package com.mojang.mojam.entity.building;

import com.mojang.mojam.entity.mob.Bat;
import com.mojang.mojam.entity.mob.Mob;

public class SpawnerForBat extends SpawnerEntity {

	
	public static final int COLOR = 0x006600;

	public SpawnerForBat(double x, double y) {
		super(x, y);
		deathPoints = 5;
	}

	@Override
	protected Mob getMob(double x, double y) {
		return new Bat(x,y);
	}

}
