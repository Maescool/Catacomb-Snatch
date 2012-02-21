package com.mojang.mojam.entity;

import com.mojang.mojam.entity.mob.Mob;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Screen;

public class BulletRed extends Bullet {

	public BulletRed(Mob e, double xa, double ya) {
		super(e, xa, ya);
		  life = 20;
		  damage=2;
	}

	@Override
	public void render(Screen screen) {
		  screen.blit(Art.bulletred[facing][0], pos.x - 8, pos.y - 10);
	}
}
