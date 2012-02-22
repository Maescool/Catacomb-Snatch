package com.mojang.mojam.entity;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.entity.mob.*;
import com.mojang.mojam.screen.*;

public class RifleBullet extends Bullet {
	public RifleBullet(Mob e, double xa, double ya, float damage) {
		super(e, xa, ya, 40, damage);
	}
}