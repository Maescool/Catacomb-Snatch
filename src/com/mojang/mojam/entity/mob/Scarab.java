package com.mojang.mojam.entity.mob;

import com.mojang.mojam.network.TurnSynchronizer;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.AbstractBitmap;

public class Scarab extends HostileMob {

	public static final int COLOR = 0xffccff00;

	public Scarab(double x, double y) {
		super(x, y, Team.Neutral);
		setPos(x, y);
		dir = TurnSynchronizer.synchedRandom.nextDouble() * Math.PI * 2;
		minimapColor = 0xffff0000;
		yOffs = 10;
		facing = TurnSynchronizer.synchedRandom.nextInt(4);
	}

	public void tick() {
		super.tick();
		if (freezeTime > 0) {
			return;
		}
		walk();
	}

	public void die() {
		super.die();
	}

	public AbstractBitmap getSprite() {
		return Art.scarab[((stepTime / 6) & 3)][(facing + 1) & 3];
	}

	@Override
	public String getDeathSound() {
		return "/sound/Enemy Death 1.wav";
	}

	@Override
	public int getColor() {
		return COLOR;
	}

	@Override
	public int getMiniMapColor() {
		return COLOR;
	}

	@Override
	public String getName() {
		return "SCARAB";
	}

	@Override
	public AbstractBitmap getBitMapForEditor() {
		return Art.scarab[0][0];
	}
}
