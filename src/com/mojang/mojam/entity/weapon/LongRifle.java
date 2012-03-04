package com.mojang.mojam.entity.weapon;

import com.mojang.mojam.entity.Player;

public class LongRifle extends Weapon {
	
	private static float bulletDamage = .85f;
	private static float accuracy = .35f;
	
	public LongRifle(Player owner) {
		super(owner, bulletDamage, accuracy);
		shootDelay = 6;
		setSoundDir("/sound/Shot 1.wav");
	}

}
