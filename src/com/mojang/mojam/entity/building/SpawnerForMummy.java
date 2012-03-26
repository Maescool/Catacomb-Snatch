package com.mojang.mojam.entity.building;

import com.mojang.mojam.entity.mob.Mob;
import com.mojang.mojam.entity.mob.Mummy;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.AbstractBitmap;

public class SpawnerForMummy extends SpawnerEntity {

	public static final int COLOR = 0xff00CC00;

	public SpawnerForMummy(double x, double y) {
		super(x, y);
		deathPoints = 15;
	}

	@Override
	protected Mob getMob(double x, double y) {
		return new Mummy(x,y);
	}
	
	@Override
	public int getColor() {
		return SpawnerForMummy.COLOR;
	}

	@Override
	public int getMiniMapColor() {
		return SpawnerForMummy.COLOR;
	}

	@Override
	public String getName() {
		return "MUMMYS";
	}
	
	@Override
	public AbstractBitmap getBitMapForEditor() {
		AbstractBitmap shrink = Art.mummy[0][3].shrink();
		AbstractBitmap bitmap = blitMobOnTop(shrink);
		return bitmap;
	}
}
