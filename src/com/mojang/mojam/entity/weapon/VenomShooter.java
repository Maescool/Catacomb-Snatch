package com.mojang.mojam.entity.weapon;

import com.mojang.mojam.entity.Bullet;
import com.mojang.mojam.entity.BulletPoison;
import com.mojang.mojam.entity.mob.Mob;


public class VenomShooter extends Rifle {

	public VenomShooter(Mob owner) {
		super(owner);
	}

	public Bullet getAmmo(double xDir, double yDir) {
		Bullet bullet = new BulletPoison(owner, xDir, yDir, bulletDamage);
		if (!(owner instanceof Player))
			bullet.pos.y = bullet.pos.y-19; //this will make the bullet look like its coming out of the snakes mouth
		return bullet;
	}
	
}
