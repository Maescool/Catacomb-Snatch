package com.mojang.mojam.entity.particle;

import com.mojang.mojam.network.TurnSynchronizer;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.AbstractScreen;

public class Sparkle extends Particle {
	public int duration;

	public Sparkle(double x, double y, double xa, double ya) {
		super(x, y, xa, ya);
		duration = (life = TurnSynchronizer.synchedRandom.nextInt(10) + 20) + 1;
	}

	public void render(AbstractScreen screen) {
		int anim = Art.shineBig.length - life * Art.shineBig.length / duration
				- 1;
		screen.blit(Art.shineBig[anim][0], (int) (pos.x - 8), (int) (pos.y - 12 - z));
	}
}