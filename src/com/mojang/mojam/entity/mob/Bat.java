package com.mojang.mojam.entity.mob;

import com.mojang.mojam.level.HoleTile;
import com.mojang.mojam.network.TurnSynchronizer;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Bitmap;
import com.mojang.mojam.screen.Screen;

public class Bat extends HostileMob {
	private int tick = 0;

	public Bat(double x, double y, int localTeam) {
		super(x, y, Team.Neutral, localTeam);
		setPos(x, y);
		setStartHealth(1);
		dir = TurnSynchronizer.synchedRandom.nextDouble() * Math.PI * 2;
		minimapColor = 0xffff0000;
		yOffs = 5;
		deathPoints = 1;
		strength = 1;
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
		if (tick % 2 == 0) {
			if(!(level.getTile(pos) instanceof HoleTile))
			screen.blit(Art.batShadow, pos.x - Art.batShadow.w / 2, pos.y
					- Art.batShadow.h / 2 - yOffs + 16);			
		}
		super.render(screen);

	}
}
