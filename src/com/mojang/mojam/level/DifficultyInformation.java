package com.mojang.mojam.level;

import com.mojang.mojam.MojamComponent;

public enum DifficultyInformation {
	EASY(MojamComponent.texts.getStatic("diffselect.easy"), .5f, .5f, 1.5f, .5f, false, 25, 3),
	NORMAL(MojamComponent.texts.getStatic("diffselect.normal"), 1, 1, 1, 1, false, 25, 7),
	HARD(MojamComponent.texts.getStatic("diffselect.hard"), 3, 3, .5f, 1.5f, true, 25, 12),
	NIGHTMARE(MojamComponent.texts.getStatic("diffselect.nightmare"), 6, 5, .25f, 2.5f, true, 15, 100000);
	
	private String difficultyName;

	private final float mobHealthModifier;
	private final float mobStrengthModifier;
	private final float mobSpawnModifier;
	private final float shopCostsModifier;
	private boolean mobRegenerationAllowed;
	private int regenerationInterval;
	private int allowedMobDensity;

	private DifficultyInformation(String difficultyName, float mobHealthModifier, float mobStrengthModifier, float mobSpawnModifier, float shopCostsModifier, boolean mobRegeneration, int regenerationInterval, int allowedMobDensity) {
		this.difficultyName = difficultyName;
		this.mobHealthModifier = mobHealthModifier;
		this.mobStrengthModifier = mobStrengthModifier;
		this.mobSpawnModifier = mobSpawnModifier;
		this.shopCostsModifier = shopCostsModifier;
		this.mobRegenerationAllowed = mobRegeneration;
		this.regenerationInterval = regenerationInterval;
		this.allowedMobDensity = allowedMobDensity;
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
		DifficultyInformation found = null;
		for(DifficultyInformation curDiff : DifficultyInformation.values()){
			if(curDiff.ordinal() == ordinal){
				found = curDiff;
			}
		}
		return found;
	}
}
