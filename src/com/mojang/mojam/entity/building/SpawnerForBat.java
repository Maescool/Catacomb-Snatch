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
		Bitmap bitmap = Art.mobSpawner[0][0].clone();
		Bitmap shrink = Bitmap.shrink(Art.bat[0][0]);
		bitmap.blit(shrink, (bitmap.w/2)-shrink.w/2,(bitmap.h/2)-5-shrink.h/2);
		return bitmap;
	}

}
