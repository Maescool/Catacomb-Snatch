package com.mojang.mojam.entity.mob;

import com.mojang.mojam.entity.Entity;
import com.mojang.mojam.gui.TitleMenu;
import com.mojang.mojam.network.TurnSynchronizer;
import com.mojang.mojam.screen.*;

public class Bat extends Mob {
	private int tick = 0;

	public Bat(double x, double y) {
		super(x, y, Team.Neutral);
		setPos(x, y);
		setStartHealth(1 + TitleMenu.Difficulty);
		dir = TurnSynchronizer.synchedRandom.nextDouble() * Math.PI * 2;
		minimapColor = 0xffff0000;
		yOffs = 5;
		deathPoints = 1;
	}

	public void tick() {
		super.tick();
		if (freezeTime > 0)
			return;

		tick++;

		dir += (TurnSynchronizer.synchedRandom.nextDouble() - TurnSynchronizer.synchedRandom
				.nextDouble()) * 0.2;
		xd += Math.cos(dir) * 1;
		yd += Math.sin(dir) * 1;
		
		if (shouldBounceOffWall(xd, yd)){
			xd = -xd;
			yd = -yd;
		}
		
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

	public Bitmap getSprite() {
		return Art.bat[(tick / 3) & 3][0];
	}

	@Override
	public void render(Screen screen) {
		if (tick % 2 == 0)
			screen.blit(Art.batShadow, pos.x - Art.batShadow.w / 2, pos.y
					- Art.batShadow.h / 2 - yOffs + 16);
		super.render(screen);

	}

	@Override
	public void collide(Entity entity, double xa, double ya) {
		super.collide(entity, xa, ya);

		if (entity instanceof Mob) {
			Mob mob = (Mob) entity;
			if (isNotFriendOf(mob)) {
				mob.hurt(this, 1);
			}
		}
	}
}
