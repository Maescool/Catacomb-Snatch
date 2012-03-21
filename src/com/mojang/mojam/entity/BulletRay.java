package com.mojang.mojam.entity;

import com.mojang.mojam.entity.mob.*;
import com.mojang.mojam.network.TurnSynchronizer;
import com.mojang.mojam.screen.AbstractScreen;
import com.mojang.mojam.screen.Art;

public class BulletRay extends Bullet {
	private int maxBounceNumber;
	private double previuosPositionX, previuosPositionY;
	private int frame;

	public BulletRay(Mob e, double xa, double ya, float damage) {
		super(e, ya, ya, damage);
		this.owner = e;
		pos.set(e.pos.x + xa * 4, e.pos.y + ya * 4);
		this.xa = xa * 5;
		this.ya = ya * 5;
		this.setSize(4, 4);
		physicsSlide = false;
		duration = 50;
		maxBounceNumber = 5;
		frame = 0;
		this.damage = damage;
	}

	@Override
	public void tick() {
		previuosPositionX = pos.x;
		previuosPositionY = pos.y;
		if (--duration <= 0) {
			remove();
			return;
		}
		if (!move(xa, ya)) {
			if(maxBounceNumber > 0) {
				//Bounce
				if(previuosPositionX != pos.x) {
					ya = -ya;
				} 
				if(previuosPositionY != pos.y) {
					xa = -xa;
				} 
				if(previuosPositionY == pos.y && previuosPositionX == pos.x) {
					xa = -xa;
					ya = -ya;
				}
				
				//Increase the speed, duration and damage with each bounce
				xa *= 1.2;
				ya *= 1.2;
				duration += 5;
				damage *= 1.5;

				maxBounceNumber--;
			}
			else hit = true;
		}
		if (hit && !removed) {
			remove();
		}
		frame = TurnSynchronizer.synchedRandom.nextInt(8);
	}

	@Override
	public void render(AbstractScreen screen) {
		screen.blit(Art.plasmaBall[frame][0], (int)pos.x - 8, (int)pos.y - 10);
	}
}
