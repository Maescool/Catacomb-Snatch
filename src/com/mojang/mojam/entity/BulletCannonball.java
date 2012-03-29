package com.mojang.mojam.entity;

import java.util.Set;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.entity.animation.LargeBombExplodeAnimation;
import com.mojang.mojam.entity.mob.Mob;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.AbstractScreen;

public class BulletCannonball extends Bullet
{
	
	public BulletCannonball(Mob e, double xa, double ya, float damage)
	{
		super(e, xa, ya, damage);
	}

	@Override
	public void tick()
	{
		if (--duration <= -20) {
			remove();
			return;
		}
		if(!move(xa,ya))
		{
			if(move(-xa,ya))xa = -xa;
			if(move(xa,-ya))ya = -ya;
		}
		xa *= 0.95;
		ya *= 0.95;
	}
	
	@Override
	public void remove()
	{
		level.addEntity(new LargeBombExplodeAnimation(pos.x, pos.y));
		MojamComponent.soundPlayer.playSound("/sound/Explosion 2.wav",
				(float) pos.x, (float) pos.y);
		float BOMB_DISTANCE = 100;
		Set<Entity> entities = level.getEntities(pos.x - BOMB_DISTANCE, pos.y
				- BOMB_DISTANCE, pos.x + BOMB_DISTANCE, pos.y + BOMB_DISTANCE,
				Mob.class);
		for (Entity e : entities) {
			double distSqr = pos.distSqr(e.pos);
			if (distSqr < (BOMB_DISTANCE * BOMB_DISTANCE)) {
				((Mob) e).hurt(this, (float) (damage*damage/distSqr));
			}
		}
		super.remove();
	}
	
	@Override
	public void render(AbstractScreen screen)
	{
		screen.blit(Art.bomb, pos.x-16, pos.y-16);
	}
	
}
