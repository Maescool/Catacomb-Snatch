package com.mojang.mojam.entity.animation;

import com.mojang.mojam.entity.mob.Mob;
import com.mojang.mojam.math.Mth;
import com.mojang.mojam.screen.AbstractBitmap;
import com.mojang.mojam.screen.AbstractScreen;

public class SmokePuffAnimation extends Animation {
	AbstractBitmap[][] bitmap;
	int width;
	int numFrames;
	double xa, ya;

	public SmokePuffAnimation(Mob mob, AbstractBitmap[][] bitmap, int duration) {
		super(mob.pos.x, mob.pos.y, duration);
		xa = mob.xd * 0.5f;
		ya = mob.yd * 0.5f;
		pos.x -= xa + 6;
		pos.y -= ya;
		this.bitmap = bitmap;
		width = bitmap.length;
		numFrames = width * bitmap[0].length;
	}
	
	public SmokePuffAnimation(double x, double y, double xa, double ya, Bitmap[][] bitmap, int duration) {
		super(x, y, duration);
		this.xa = xa;
		this.ya = ya;
		this.bitmap = bitmap;
		width = bitmap.length;
		numFrames = width * bitmap[0].length;
	}
	
	public void tick() {
		super.tick();
		move(xa, ya);
		xa *= 0.9f;
		ya *= 0.9f;
	}

	public void render(AbstractScreen screen) {
		int frame = Mth.clamp(numFrames - life * numFrames / duration - 1, 0,
				numFrames);
		screen.blit(bitmap[frame % width][frame / width], pos.x, pos.y);
	}
}
