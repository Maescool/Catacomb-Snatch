package com.mojang.mojam.entity.weapon;

import com.mojang.mojam.Options;
import com.mojang.mojam.entity.Bullet;
import com.mojang.mojam.entity.mob.Mob;
import com.mojang.mojam.screen.Screen;

public class Machete extends Melee
{

	public Machete(Mob mob)
	{
		super(mob);
	}
	
	public void setWeaponMode()
	{
		if(Options.getAsBoolean(Options.CREATIVE))
		{
			BULLET_DAMAGE = 100f;
		}
		else
		{
			BULLET_DAMAGE = 2f;
		}
	}
	
	public Bullet getAmmo(double xDir, double yDir)
	{
		Bullet bullet = new Bullet(owner, xDir, yDir, BULLET_DAMAGE)
		{
			@Override
			public void tick()
			{
				if (--duration <= 18) {
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
		};
		return bullet;
	}

}
