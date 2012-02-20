package com.mojang.mojam.entity.animation;

import com.mojang.mojam.network.TurnSynchronizer;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Bitmap;
import com.mojang.mojam.screen.Screen;

public class BombExplodeAnimationSmall extends Animation {
    public double z;

    public BombExplodeAnimationSmall(double x, double y) {
        this(x, y, 0);
    }

    public BombExplodeAnimationSmall(double x, double y, double z) {
        super(x, y, TurnSynchronizer.synchedRandom.nextInt(10) + 20); // @random
        this.z = z;
    }

    public void render(Screen screen) {
        Bitmap[][] bmps = Art.fxBombSplosionSmall;
        int anim = bmps.length - life * bmps.length / duration - 1;
        screen.blit(bmps[anim][0], pos.x - bmps[0][0].w / 2, pos.y - bmps[0][0].h / 2 - 4 - z);
    }
}
