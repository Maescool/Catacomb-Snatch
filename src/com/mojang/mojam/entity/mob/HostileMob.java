package com.mojang.mojam.entity.mob;

import com.mojang.mojam.level.DifficultyInformation;


public abstract class HostileMob extends Mob {

	public HostileMob(double x, double y, int team) {
		super(x, y, team);
	}
	
	@Override
	public void setStartHealth(float newHealth) {
		super.setStartHealth(DifficultyInformation.calculateHealth(newHealth));
	}
}
