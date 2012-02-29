package com.mojang.mojam.entity.building;

import com.mojang.mojam.entity.mob.Mob;
import com.mojang.mojam.entity.mob.Snake;

public class SpawnerForSnake extends SpawnerEntity {

	public static final int COLOR = 0x009900;

	public SpawnerForSnake(double x, double y) {
		super(x, y);
		deathPoints = 10;
	}

	@Override
	protected Mob getMob(double x, double y) {
		return new Snake(x, y);
	}
	
	
	

}
