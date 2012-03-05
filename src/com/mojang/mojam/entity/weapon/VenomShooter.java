package com.mojang.mojam.entity.weapon;

import com.mojang.mojam.entity.Bullet;
import com.mojang.mojam.entity.BulletPoison;
import com.mojang.mojam.entity.mob.Mob;


public class VenomShooter extends Weapon {
	
	private static float BULLET_DAMAGE = .35f;
	private static float ACCURACY = .15f;
	private static int SHOT_DELAY = 5;

	public VenomShooter(Mob owner) {
		super(owner);
		setWeaponMode(BULLET_DAMAGE, ACCURACY, SHOT_DELAY);
	}
	
	public Bullet getAmmo(double xDir, double yDir) {
		Bullet bullet = new BulletPoison(owner, xDir, yDir, BULLET_DAMAGE);
		bullet.pos.y = bullet.pos.y - 19; //this will make the bullet look like its coming out of the snakes mouth
		bullet.pos.x = bullet.pos.x;
		return bullet;
	}
	
}