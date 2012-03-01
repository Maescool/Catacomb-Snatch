package com.mojang.mojam.entity.building;

import com.mojang.mojam.entity.mob.Bat;
import com.mojang.mojam.entity.mob.Mob;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Bitmap;

public class SpawnerForBat extends SpawnerEntity {

	
	public static final int COLOR = 0xff006600;

	public SpawnerForBat(double x, double y) {
		super(x, y);
		deathPoints = 5;
	}

	@Override
	protected Mob getMob(double x, double y) {
		return new Bat(x,y);
	}

	@Override
	public int getColor() {
		// TODO Auto-generated method stub
		return SpawnerForBat.COLOR;
	}

	@Override
	public int getMiniMapColor() {
		// TODO Auto-generated method stub
		return SpawnerForBat.COLOR;
	}

	@Override
	public String getName() {
		return "BATS";
	}

	@Override
	public Bitmap getBitMapForEditor() {
		return Art.bat[0][0];
	}

}
