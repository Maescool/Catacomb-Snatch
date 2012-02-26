package com.mojang.mojam.entity.weapon;

import com.mojang.mojam.entity.Player;

public interface IWeapon {
	void setOwner(Player player);

	void weapontick();

	void upgradeWeapon();

	void primaryFire(double xDir, double yDir);
}
