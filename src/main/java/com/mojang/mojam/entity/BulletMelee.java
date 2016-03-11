package com.mojang.mojam.entity;

import com.mojang.mojam.entity.mob.Mob;
import com.mojang.mojam.screen.AbstractScreen;

public class BulletMelee extends Bullet
{

	public int color = 0xffcc9966;
	
	public BulletMelee(Mob e, double xa, double ya, float damage, int range)
	{
		super(e, xa, ya, damage);
		duration = range;
	}

	public BulletMelee(Mob e, double xa, double ya, float damage, int range, int color)
	{
		this(e, xa, ya, damage, range);
		this.color = color;
	}

	@Override
	public void tick()
	{
		if(--duration <= 0)
		{
			remove();
			return;
		}
		super.tick();
	}

	@Override
	public void render(AbstractScreen screen)
	{
		screen.rectangle((int) pos.x - 3, (int) pos.y - 3, 6, 6, color);
		screen.rectangle((int) pos.x - 2, (int) pos.y - 2, 4, 4, color);
		screen.rectangle((int) pos.x - 1, (int) pos.y - 1, 2, 2, color);
	}

}
