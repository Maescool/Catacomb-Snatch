package com.mojang.mojam.level;

import com.mojang.mojam.MojamComponent;

public enum DifficultyInformation {
	EASY(MojamComponent.texts.getStatic("diffselect.easy"), .5f, .5f, 1.5f, .5f, false, 25, 3, 30),
	NORMAL(MojamComponent.texts.getStatic("diffselect.normal"), 1, 1, 1, 1, false, 25, 7, 20),
	HARD(MojamComponent.texts.getStatic("diffselect.hard"), 3, 3, .5f, 1.5f, true, 25, 12, 15),
	NIGHTMARE(MojamComponent.texts.getStatic("diffselect.nightmare"), 6, 5, .25f, 2.5f, true, 15, 100000, 10);
	
	private String difficultyName;

	private final float mobHealthModifier;
	private final float mobStrengthModifier;
	private final float mobSpawnModifier;
	private final float shopCostsModifier;
	private boolean mobRegenerationAllowed;
	private int regenerationInterval;
	private int allowedMobDensity;
	private int coinLifespan;

	private DifficultyInformation(String difficultyName, float mobHealthModifier, float mobStrengthModifier, float mobSpawnModifier, float shopCostsModifier, boolean mobRegeneration, int regenerationInterval, int allowedMobDensity,int coinLifespan) {
		this.difficultyName = difficultyName;
		this.mobHealthModifier = mobHealthModifier;
		this.mobStrengthModifier = mobStrengthModifier;
		this.mobSpawnModifier = mobSpawnModifier;
		this.shopCostsModifier = shopCostsModifier;
		this.mobRegenerationAllowed = mobRegeneration;
		this.regenerationInterval = regenerationInterval;
		this.allowedMobDensity = allowedMobDensity;
		this.coinLifespan = coinLifespan;
	}

	public float calculateHealth(float baseHealth) {
			return baseHealth * this.mobHealthModifier;
	}

	public float calculateStrength(int baseStrength) {
			return baseStrength * this.mobStrengthModifier;
	}

	public int calculateSpawntime(int baseSpawntime) {
			return (int)(baseSpawntime * this.mobSpawnModifier);
	}

	public int calculateCosts(int baseCost) {
			return (int)(baseCost * this.shopCostsModifier);
	}
	
	public int getCoinLifespan() {
		return this.coinLifespan;
}

	public String getDifficultyName() {
		return difficultyName;
	}

	public boolean isMobRegenerationAllowed() {
		return mobRegenerationAllowed;
	}

	public int getRegenerationInterval() {
		return regenerationInterval;
	}

	public int getAllowedMobDensity() {
		return allowedMobDensity;
	}
	
	/*
	 * Look up an enum by the ordinal
	 */
	public static DifficultyInformation getByInt(int ordinal){
		return DifficultyInformation.values()[ordinal];
	}
}
