package com.mojang.mojam.entity.mob;

import com.mojang.mojam.entity.Entity;
import com.mojang.mojam.gui.TitleMenu;
import com.mojang.mojam.level.DifficultyInformation;
import com.mojang.mojam.network.TurnSynchronizer;
import com.mojang.mojam.screen.*;

public class Mummy extends HostileMob {
	public int facing;
	public int walkTime;
	public int stepTime;

	public Mummy(double x, double y) {
		super(x, y, Team.Neutral);
		setPos(x, y);
		setStartHealth(7);
		dir = TurnSynchronizer.synchedRandom.nextDouble() * Math.PI * 2;
		minimapColor = 0xffff0000;
		yOffs = 10;
		facing = TurnSynchronizer.synchedRandom.nextInt(4);

		deathPoints = 4;
	}

	public void tick() {
		super.tick();
		if (freezeTime > 0)
			return;

		double speed = 0.5;
		if (facing == 0)
			yd += speed;
		if (facing == 1)
			xd -= speed;
		if (facing == 2)
			yd -= speed;
		if (facing == 3)
			xd += speed;
		walkTime++;

		if (walkTime / 12 % 3 != 0) {
			if (shouldBounceOffWall(xd, yd)){
				facing = facing+2%4;
				xd = -xd;
				yd = -yd;
			}
			
			stepTime++;
			if (!move(xd, yd)
					|| (walkTime > 10 && TurnSynchronizer.synchedRandom
							.nextInt(200) == 0)) {
				facing = TurnSynchronizer.synchedRandom.nextInt(4);
				walkTime = 0;
			}
		}
		xd *= 0.2;
		yd *= 0.2;
	}

	public void die() {
		super.die();
	}

	public Bitmap getSprite() {
		return Art.mummy[((stepTime / 6) & 3)][(facing + 3) & 3];
	}

	@Override
	public void collide(Entity entity, double xa, double ya) {
		super.collide(entity, xa, ya);

		if (entity instanceof Mob) {
			Mob mob = (Mob) entity;
			if (isNotFriendOf(mob)) {
				mob.hurt(this, DifficultyInformation.calculateStrength(2));
			}
		}
	}

	@Override
	public String getDeatchSound() {
		return "/sound/Enemy Death 2.wav";
	}
}
