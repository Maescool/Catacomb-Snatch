package com.mojang.mojam.entity.animation;

import com.mojang.mojam.network.TurnSynchronizer;
import com.mojang.mojam.screen.Screen;

public class TileExplodeAnimation extends Animation {
    public TileExplodeAnimation(double x, double y) {
        super(x, y, TurnSynchronizer.synchedRandom.nextInt(10) + 20); // @random
    }

    @Override
    public void tick() {
        super.tick();

        double x = pos.x + TurnSynchronizer.synchedRandom.nextDouble() * 32 - 16;
        double y = pos.y + TurnSynchronizer.synchedRandom.nextDouble() * 32 - 16;
        double z = TurnSynchronizer.synchedRandom.nextDouble() * 24;

        level.addEntity(new BombExplodeAnimationSmall(x, y, z));
    }

    public void render(Screen screen) {
    }
}
