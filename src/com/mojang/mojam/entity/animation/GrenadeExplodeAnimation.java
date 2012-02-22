package com.mojang.mojam.entity.animation;

import com.mojang.mojam.entity.Bullet;

import com.mojang.mojam.entity.Entity;
import com.mojang.mojam.network.TurnSynchronizer;
import com.mojang.mojam.screen.Screen;

public class GrenadeExplodeAnimation extends Animation {
	
	private final Bullet source;
	private final int radius;
	
	public GrenadeExplodeAnimation(double x, double y, int radius, Bullet source) {
		super(x, y, TurnSynchronizer.synchedRandom.nextInt(20) + 15); // @random
		this.source = source;
		this.radius = radius;
	}

	@Override
	public void tick() {
		super.tick();
		double dir = TurnSynchronizer.synchedRandom.nextDouble() * Math.PI * 2;

		int maxRadius = (16 - life * 16 / duration) + 4;

		double dist = TurnSynchronizer.synchedRandom.nextDouble() * maxRadius;

		double x = pos.x + Math.cos(dir) * dist;
		double y = pos.y + Math.sin(dir) * dist;

		if (TurnSynchronizer.synchedRandom.nextInt(duration) <= life)
			level.addEntity(new BombExplodeAnimation(x, y));
		else
			level.addEntity(new BombExplodeAnimationSmall(x, y));

		if (life == 10) {
			for (Entity e : level.getEntities(getBB().grow(radius * 32))) {
				e.hurt(source);
			}
		}
	}
	
	@Override
	public void render(Screen screen) {
	}
}
