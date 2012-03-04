package com.mojang.mojam.entity.weapon;

import com.mojang.mojam.entity.Bullet;
import com.mojang.mojam.entity.BulletPoison;
import com.mojang.mojam.entity.mob.Mob;

public class VenomRifle extends Weapon {

	private static float BULLET_DAMAGE = .90f;
	private static float ACCURACY = .12f;
	private static int SHOT_DELAY = 5;
	
	public VenomRifle(Mob owner) {
		super(owner);
		setWeaponMode(BULLET_DAMAGE, ACCURACY, SHOT_DELAY);
	}
	
	public Bullet getAmmo(double xDir, double yDir) {
		Bullet bullet = new BulletPoison(owner, xDir, yDir, BULLET_DAMAGE);
		return bullet;
	}

}
