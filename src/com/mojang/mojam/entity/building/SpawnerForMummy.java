package com.mojang.mojam.entity.building;

import com.mojang.mojam.entity.mob.Mob;
import com.mojang.mojam.entity.mob.Mummy;

public class SpawnerForMummy extends SpawnerEntity {

	public static final int COLOR = 0x00CC00;

	public SpawnerForMummy(double x, double y) {
		super(x, y);
		deathPoints = 15;
	}

	@Override
	protected Mob getMob(double x, double y) {
		return new Mummy(x,y);
	}

}
