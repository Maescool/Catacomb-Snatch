package com.mojang.mojam.entity.weapon;

import com.mojang.mojam.entity.Player;

public class Rifle extends Weapon {
	
	private static float bulletDamage = .5f;
	private static float accuracy = .15f;
	
	public Rifle(Player owner) {
		super(owner, bulletDamage, accuracy);
		setSoundDir("/sound/Shot 1.wav");
	}

}
