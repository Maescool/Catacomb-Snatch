package com.mojang.mojam.entity.weapon;

import com.mojang.mojam.entity.Bullet;
import com.mojang.mojam.entity.mob.Mob;
import com.mojang.mojam.screen.AbstractBitmap;

public interface IWeapon {
	void setOwner(Mob mob);
	void weapontick();
	void upgradeWeapon();
	void primaryFire(double xDir, double yDir);
	Bullet getAmmo(double xDir, double yDir);
	public AbstractBitmap getSprite();
	void setWeaponMode();
}
