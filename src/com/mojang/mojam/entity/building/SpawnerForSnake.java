package com.mojang.mojam.entity.building;

import com.mojang.mojam.entity.mob.Mob;
import com.mojang.mojam.entity.mob.Snake;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Bitmap;

public class SpawnerForSnake extends SpawnerEntity {

	public static final int COLOR = 0xff009900;

	public SpawnerForSnake(double x, double y) {
		super(x, y);
		deathPoints = 10;
	}

	@Override
	protected Mob getMob(double x, double y) {
		return new Snake(x, y);
	}

	
	@Override
	public int getColor() {
		return SpawnerForSnake.COLOR;
	}

	@Override
	public int getMiniMapColor() {
		return SpawnerForSnake.COLOR;
	}

	@Override
	public String getName() {
		return "SNAKES";
	}

	@Override
	public Bitmap getBitMapForEditor() {
		return Art.snake[0][0];
	}
}
