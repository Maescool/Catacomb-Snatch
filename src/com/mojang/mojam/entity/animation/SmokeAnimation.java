package com.mojang.mojam.entity.animation;

import com.mojang.mojam.math.Mth;
import com.mojang.mojam.screen.AbstractBitmap;
import com.mojang.mojam.screen.AbstractScreen;

public class SmokeAnimation extends Animation {

	AbstractBitmap[][] bitmap;
	int width;
	int numFrames;

	public SmokeAnimation(double x, double y, AbstractBitmap[][] bitmap, int duration) {
		super(x, y, duration);
		this.bitmap = bitmap;
		width = bitmap.length;
		numFrames = width * bitmap[0].length;
	}

	public void tick() {
		move(0, -1);
		super.tick();
	}

	public void render(AbstractScreen screen) {
		int frame = Mth.clamp(numFrames - life * numFrames / duration - 1, 0,
			numFrames);
		screen.blit(bitmap[frame % width][frame / width], (int) pos.x, (int) pos.y);
	}
}
