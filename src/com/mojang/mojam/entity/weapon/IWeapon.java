package com.mojang.mojam.entity.weapon;

import com.mojang.mojam.entity.Bullet;
import com.mojang.mojam.entity.mob.Mob;

public interface IWeapon {
	void setOwner(Mob mob);
	void weapontick();
	void upgradeWeapon();
	void primaryFire(double xDir, double yDir);
	Bullet getAmmo(double xDir, double yDir);
}
