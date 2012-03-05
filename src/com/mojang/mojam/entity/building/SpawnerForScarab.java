package com.mojang.mojam.entity.building;

import com.mojang.mojam.entity.mob.Mob;
import com.mojang.mojam.entity.mob.Scarab;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Bitmap;

public class SpawnerForScarab extends SpawnerEntity {


	public static final int COLOR = 0xff00ff00;

	public SpawnerForScarab(double x, double y) {
		super(x, y);
		deathPoints = 20;
	}

	@Override
	protected Mob getMob(double x, double y) {
		return new Scarab(x,y);
	}
	
	@Override
	public int getColor() {
		return SpawnerForScarab.COLOR;
	}

	@Override
	public int getMiniMapColor() {
		return SpawnerForScarab.COLOR;
	}

	@Override
	public String getName() {
		return "SCARABS";
	}
	
	@Override
	public Bitmap getBitMapForEditor() {
		return Art.scarab[0][1];
	}
}
