package com.mojang.mojam.buff;

import com.mojang.mojam.buff.Buff;

class Poison extends Buff {
	public BuffType buffType = BuffType.HEALTH_MODIF;
	
	public Poison(int duration, float how) {
		super(duration,-how);
	}
}