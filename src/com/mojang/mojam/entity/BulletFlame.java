package com.mojang.mojam.entity;

import com.mojang.mojam.entity.mob.Mob;
import com.mojang.mojam.network.TurnSynchronizer;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.AbstractBitmap;
import com.mojang.mojam.screen.AbstractScreen;

public class BulletFlame extends Bullet {
	
	boolean shrink=false;

	public BulletFlame(Mob e, double xa, double ya, float damage) {
		super(e, xa, ya, damage);
		
		this.owner = e;
		pos.set(e.pos.x + xa * 4, e.pos.y + ya * 4);
		this.xa = xa * ((4 * TurnSynchronizer.synchedRandom.nextFloat())+1);
		this.ya = ya * ((4 * TurnSynchronizer.synchedRandom.nextFloat())+1);
		this.setSize(4, 4);
		physicsSlide = false;
		duration = 27;
		double angle = (Math.atan2(ya, xa) + Math.PI * 1.625);
		facing = (8 + (int) (angle / Math.PI * 4)) & 7;
		this.damage = damage;
		
		if(TurnSynchronizer.synchedRandom.nextInt(4)==0)
			shrink=true;
		
		freezeTime = 0; //this allows other bullets to damage immediately instead of waiting 5 ticks
	}

	@Override
	public void render(AbstractScreen screen) {
		
		if(shrink)
			screen.blit(screen.shrink(Art.bulletflame[facing][0]), pos.x - 8, pos.y - 10);
		else
			screen.blit(Art.bulletflame[facing][0], pos.x - 8, pos.y - 10);
		
	}
	
}
