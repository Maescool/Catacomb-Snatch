package com.mojang.mojam.buff;


public class Poison extends Buff {
	public BuffType buffType = BuffType.HEALTH_MODIF;
	
	public Poison(int duration, float how) {
		super(duration,-how);
	}
}