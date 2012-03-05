package com.mojang.mojam.entity.weapon;

import com.mojang.mojam.Options;
import com.mojang.mojam.entity.Bullet;
import com.mojang.mojam.entity.BulletRay;
import com.mojang.mojam.entity.mob.Mob;
import com.mojang.mojam.screen.Art;

public class Raygun extends Rifle {

	public Raygun(Mob owner) {
		super(owner);
		shootDelay = 10;
		image = Art.weaponList[2][0];
		setWeaponMode();
	}
	
	@Override
	public void setWeaponMode(){
		if(Options.getAsBoolean(Options.CREATIVE)){
			bulletDamge = 100f;
			accuracy = 0;
		}else{
			bulletDamge = 1.5f;
			accuracy = 0.05;
		}
	}

	public Bullet getAmmo(double xDir, double yDir) {
		Bullet bullet = new BulletRay(owner, xDir, yDir, bulletDamge);
		return bullet;
	}
	
}