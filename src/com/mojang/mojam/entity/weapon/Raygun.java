package com.mojang.mojam.entity.weapon;

import com.mojang.mojam.Options;
import com.mojang.mojam.entity.Bullet;
import com.mojang.mojam.entity.BulletRay;
import com.mojang.mojam.entity.mob.Mob;
import com.mojang.mojam.screen.Art;

public class Raygun extends Rifle {

	public Raygun(Mob owner) {
		super(owner);
		image = Art.weaponList[2][0];
		setWeaponMode();
	}

	@Override
	public void setWeaponMode() {
		if (Options.getAsBoolean(Options.CREATIVE)) {
			bulletDamage = 100f;
			accuracy = 0;
		} else {
			super.setWeaponMode();
		}
	}

	public Bullet getAmmo(double xDir, double yDir) {
		Bullet bullet = new BulletRay(owner, xDir, yDir, bulletDamage);
		return bullet;
	}
}
