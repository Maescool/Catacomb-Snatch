package com.mojang.mojam.entity.weapon;

import com.mojang.mojam.Options;
import com.mojang.mojam.entity.Bullet;
import com.mojang.mojam.entity.BulletRay;
import com.mojang.mojam.entity.mob.Mob;
import com.mojang.mojam.screen.Art;

public class Raygun extends Rifle {

	public Raygun(Mob owner) {
		super(owner);
		setWeaponMode();
	}

	@Override
	public void setWeaponMode() {
		super.setWeaponMode();
		if (Options.getAsBoolean(Options.CREATIVE)) {
		}
	}

	public Bullet getAmmo(double xDir, double yDir) {
		Bullet bullet = new BulletRay(owner, xDir, yDir, 0);
		return bullet;
	}
}
