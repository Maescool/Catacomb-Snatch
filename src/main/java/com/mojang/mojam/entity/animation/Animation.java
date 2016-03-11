package com.mojang.mojam.entity.animation;

import com.mojang.mojam.entity.Entity;

public class Animation extends Entity {
	public int life;
	public int duration;

	public Animation(double x, double y, int duration) {
		setPos(x, y);
		isBlocking = false;
		physicsSlide = false;
		this.duration = life = duration;
	}

	public void tick() {
		if (--life < 0)
			remove();
	}
}
