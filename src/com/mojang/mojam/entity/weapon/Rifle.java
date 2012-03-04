package com.mojang.mojam.entity.weapon;

import com.mojang.mojam.entity.mob.Mob;

public class Rifle extends Weapon {

	private static float BULLET_DAMAGE = .35f;
	private static float ACCURACY = .15f;
	private static int SHOT_DELAY = 5;
	
	public Rifle(Mob owner) {
		super(owner);
		setWeaponMode(BULLET_DAMAGE, ACCURACY, SHOT_DELAY);
	}

}
