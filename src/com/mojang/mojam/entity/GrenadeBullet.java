package com.mojang.mojam.entity;

import com.mojang.mojam.entity.animation.GrenadeExplodeAnimation;
import com.mojang.mojam.entity.mob.Mob;

public class GrenadeBullet extends Bullet {

	public GrenadeBullet(Mob e, double xa, double ya, float damage) {
		super(e, xa, ya, 25, damage);
	}

	public void tick() {
		if (--life <= 0) {
			remove();
			return;
		}
		if (!move(xa, ya)) {
			// TODO: Explode
			hit = true;
		}
		if (hit && !removed) {
			remove();
		}
	}
	
	@Override
	public void remove() {
		super.remove();
		level.addEntity(new GrenadeExplodeAnimation(pos.x, pos.y, 1, this));
	}
}