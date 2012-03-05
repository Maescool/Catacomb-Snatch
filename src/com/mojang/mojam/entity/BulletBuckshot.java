package com.mojang.mojam.entity;

import com.mojang.mojam.entity.mob.*;
import com.mojang.mojam.screen.*;

public class BulletBuckshot extends Bullet {
	public double xa, ya;
	public Mob owner;
	boolean hit = false;
	public int duration;
	private float damage;

	public BulletBuckshot(Mob owner, double xa, double ya, float damage) {
		super(owner, ya, ya, damage);
		this.owner = owner;
		pos.set(owner.pos.x + xa * 4, owner.pos.y + ya * 4);
		this.xa = xa * 18;
		this.ya = ya * 18;
		this.setSize(2, 2);
		physicsSlide = false;
		duration = 20;
		this.damage = damage;
	}

	@Override
	public void tick() {
		if (--duration <= 0) {
			remove();
			return;
		}
		if (!move(xa, ya)) {
			hit = true;
		}
		if (hit && !removed) {
			remove();
		}
		if(damage > 0.5)
		damage -= 0.25;
	}

	@Override
	public void render(Screen screen) {
		screen.blit(Art.buckShot, pos.x - 8, pos.y - 8);
	}
}
