package com.mojang.mojam.entity;

import com.mojang.mojam.entity.mob.Mob;
import com.mojang.mojam.screen.Screen;

public class BulletMelee extends Bullet
{
	
	public BulletMelee(Mob e, double xa, double ya, float damage, int range)
	{
		super(e, xa, ya, damage);
		duration = range;
	}

	@Override
	public void tick()
	{
		if (--duration <= 0) {
			remove();
			return;
		}	
		super.tick();
	}
	
	@Override
	public void render(Screen screen)
	{
		//super.render(screen);
		//TODO: Effects
	}
	
}
