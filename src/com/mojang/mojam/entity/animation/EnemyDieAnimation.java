package com.mojang.mojam.entity.animation;

import com.mojang.mojam.network.TurnSynchronizer;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.AbstractScreen;

public class EnemyDieAnimation extends Animation {

	public EnemyDieAnimation(double x, double y) {
		super(x, y, TurnSynchronizer.synchedRandom.nextInt(10) + 40); // @random
	}

	public void render(AbstractScreen screen) {
		int anim = Art.fxEnemyDie.length - life * Art.fxEnemyDie.length
			/ duration - 1;
		screen.blit(Art.fxEnemyDie[anim][0], (int) pos.x - 32, (int) pos.y - 32 - 4);
	}
}
