package com.mojang.mojam.entity.animation;

import com.mojang.mojam.network.TurnSynchronizer;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Bitmap;
import com.mojang.mojam.screen.Screen;

public class BombExplodeAnimation extends Animation {
    public BombExplodeAnimation(double x, double y) {
        super(x, y, TurnSynchronizer.synchedRandom.nextInt(10) + 20); //@random
    }

    public void render(Screen screen) {
        Bitmap[][] bmps = Art.fxBombSplosion;
        int anim = bmps.length - life * bmps.length / duration - 1;
        screen.blit(bmps[anim][0], pos.x - bmps[0][0].w/2, pos.y - bmps[0][0].h/2 - 4);
    }
}
