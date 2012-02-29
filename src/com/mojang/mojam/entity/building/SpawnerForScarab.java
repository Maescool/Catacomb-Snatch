package com.mojang.mojam.entity.building;

import com.mojang.mojam.entity.mob.Mob;
import com.mojang.mojam.entity.mob.Scarab;

public class SpawnerForScarab extends SpawnerEntity {


	public static final int COLOR = 0x00FF00;

	public SpawnerForScarab(double x, double y) {
		super(x, y);
		deathPoints = 20;
	}

	@Override
	protected Mob getMob(double x, double y) {
		return new Scarab(x,y);
	}
	

}
