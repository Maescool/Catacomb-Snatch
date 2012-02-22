package com.mojang.mojam.level;

import com.mojang.mojam.gui.TitleMenu;

public class DifficultyInformation {

	public String difficultyName;

	public final float mobHealthModifier;
	public final float mobStrengthModifier;
	public final float mobSpawnModifier;
	public final float shopCostsModifier;

	public DifficultyInformation(String difficultyName, float mobHealthModifier, float mobStrengthModifier, float mobSpawnModifier, float shopCostsModifier ) {
		this.difficultyName = difficultyName;
		this.mobHealthModifier = mobHealthModifier;
		this.mobStrengthModifier = mobStrengthModifier;
		this.mobSpawnModifier = mobSpawnModifier;
		this.shopCostsModifier = shopCostsModifier;
	}

	public static float calculateHealth(float baseHealth) {
		return baseHealth * TitleMenu.difficulty.mobHealthModifier;
	}

	public static float calculateStrength(int baseStrength) {
		return baseStrength * TitleMenu.difficulty.mobStrengthModifier;
	}

	public static int calculateSpawntime(int baseSpawntime) {
		return (int)(baseSpawntime * TitleMenu.difficulty.mobSpawnModifier);
	}

	public static int calculateCosts(int baseCost) {
		return (int)(baseCost * TitleMenu.difficulty.shopCostsModifier);
	}
}
