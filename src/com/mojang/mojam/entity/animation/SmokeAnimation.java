package com.mojang.mojam.entity.animation;

import com.mojang.mojam.math.Mth;
import com.mojang.mojam.screen.Bitmap;
import com.mojang.mojam.screen.Screen;

public class SmokeAnimation extends Animation {

	Bitmap[][] bitmap;
	int width;
	int numFrames;
	
	public SmokeAnimation(double x, double y, Bitmap[][] bitmap, int duration) {
		super(x, y, duration);
		this.bitmap = bitmap;
		width = bitmap.length;
		numFrames = width * bitmap[0].length;
	}
	
    public void tick() {
    	move(0, -1);
        super.tick();
    }
    
    public void render(Screen screen) {
        int frame = Mth.clamp(numFrames - life * numFrames / duration - 1, 0, numFrames);
        screen.blit(bitmap[frame % width][frame / width], pos.x, pos.y);
    }
}
