package com.mojang.mojam.entity.weapon;

import com.mojang.mojam.Options;
import com.mojang.mojam.entity.Bullet;
import com.mojang.mojam.entity.BulletMelee;
import com.mojang.mojam.entity.mob.Mob;
import com.mojang.mojam.resources.Constants;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Bitmap;
import com.mojang.mojam.screen.Screen;

public class Machete extends Melee
{

	public Machete(Mob mob)
	{
		super(mob);
	}
	

	public void setWeaponMode()
	{
		shootDelay = Constants.getInt("attackDelay", this);
		if(Options.getAsBoolean(Options.CREATIVE))
		{
			BULLET_DAMAGE = 100f;
		}
		else
		{
			BULLET_DAMAGE = Constants.getFloat("bulletDamage", this);
		}
	}
	
	public Bullet getAmmo(double xDir, double yDir)
	{
		Bullet bullet = new BulletMelee(owner, xDir, yDir, BULLET_DAMAGE,25,0xffcccccc);
		return bullet;
	}
	
	@Override
	public Bitmap getSprite()
	{
		// TODO Auto-generated method stub
		return Art.weaponList[1][1];
	}

}
