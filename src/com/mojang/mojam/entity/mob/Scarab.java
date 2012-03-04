package com.mojang.mojam.entity.mob;

import com.mojang.mojam.network.TurnSynchronizer;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Bitmap;

public class Scarab extends HostileMob {
	public static final int COLOR = 0xffccff00;
	
	public Scarab(double x, double y) {
		super(x, y, Team.Neutral);
		setPos(x, y);
		setStartHealth(5);
		dir = TurnSynchronizer.synchedRandom.nextDouble() * Math.PI * 2;
		minimapColor = 0xffff0000;
		yOffs = 10;
		facing = TurnSynchronizer.synchedRandom.nextInt(4);
		deathPoints = 4;
		strength = 2;
		speed = 0.7;
		limp = 4;
	}

	public void tick() {
		super.tick();
		if (freezeTime > 0)
			return;
		walk();
	}

	public void die() {
		super.die();
	}

	public Bitmap getSprite() {
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
	public Bitmap getBitMapForEditor() {
		return Art.scarab[0][0];
	}
}
