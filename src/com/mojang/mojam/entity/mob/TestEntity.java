package com.mojang.mojam.entity.mob;

import com.mojang.mojam.network.TurnSynchronizer;
import com.mojang.mojam.screen.AbstractBitmap;
import com.mojang.mojam.screen.Art;

public class TestEntity extends Mob {
	public TestEntity(double x, double y) {
		super(x, y, Team.Neutral);
		setPos(x, y);
		setStartHealth(1);
		dir = TurnSynchronizer.synchedRandom.nextDouble() * Math.PI * 2;
		minimapColor = 0xffff0000;
		yOffs = 14;
	}

	public void tick() {
		super.tick();
		if (freezeTime > 0)
			return;

		dir += (TurnSynchronizer.synchedRandom.nextDouble() - TurnSynchronizer.synchedRandom
				.nextDouble()) * 0.2;
		xd += Math.cos(dir) * 1;
		yd += Math.sin(dir) * 1;
		if (!move(xd, yd)) {
			dir += (TurnSynchronizer.synchedRandom.nextDouble() - TurnSynchronizer.synchedRandom
					.nextDouble()) * 0.8;
		}
		xd *= 0.2;
		yd *= 0.2;
	}

	public void die() {
		super.die();
	}

	public AbstractBitmap getSprite() {
		int facing = (int) ((Math.atan2(xd, -yd) * 4 / (Math.PI * 2) + 2.5)) & 3;

		return Art.mummy[facing][0];
	}
}
