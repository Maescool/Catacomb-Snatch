package com.mojang.mojam.entity.weapon;

import com.mojang.mojam.Options;
import com.mojang.mojam.entity.Bullet;
import com.mojang.mojam.entity.BulletRay;
import com.mojang.mojam.entity.mob.Mob;

public class Raygun extends Rifle {

	public Raygun(Mob owner) {
		super(owner);
	}
	
	@Override
	public void setWeaponMode(){
		if(Options.getAsBoolean(Options.CREATIVE)){
			BULLET_DAMAGE = 100f;
			accuracy = 0;
		}else{
			BULLET_DAMAGE = 1.5f;
			accuracy = 0.05;
		}
		shootDelay = 10;
	}

	public Bullet getAmmo(double xDir, double yDir) {
		Bullet bullet = new BulletRay(owner, xDir, yDir, BULLET_DAMAGE);
		return bullet;
	}
	
}