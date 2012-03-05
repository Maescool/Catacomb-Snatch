package com.mojang.mojam.level;

import com.mojang.mojam.gui.TitleMenu;

public class DifficultyInformation {

	public String difficultyName;
	public int difficultyID;

	public final float mobHealthModifier;
	public final float mobStrengthModifier;
	public final float mobSpawnModifier;
	public final float shopCostsModifier;

	public DifficultyInformation(String difficultyName, float mobHealthModifier, float mobStrengthModifier, float mobSpawnModifier, float shopCostsModifier, int difficultyID) {
		this.difficultyName = difficultyName;
		this.mobHealthModifier = mobHealthModifier;
		this.mobStrengthModifier = mobStrengthModifier;
		this.mobSpawnModifier = mobSpawnModifier;
		this.shopCostsModifier = shopCostsModifier;
		this.difficultyID = difficultyID;
	}

	public static float calculateHealth(float baseHealth) {
		if(TitleMenu.difficulty != null)
			return baseHealth * TitleMenu.difficulty.mobHealthModifier;
		else
			return 0;
	}

	public static float calculateStrength(int baseStrength) {
		if(TitleMenu.difficulty != null)
			return baseStrength * TitleMenu.difficulty.mobStrengthModifier;
		else
			return 0;
	}

	public static int calculateSpawntime(int baseSpawntime) {
		if(TitleMenu.difficulty != null)
			return (int)(baseSpawntime * TitleMenu.difficulty.mobSpawnModifier);
		else
			return 0;
	}

	public static int calculateCosts(int baseCost) {
		if(TitleMenu.difficulty != null)
			return (int)(baseCost * TitleMenu.difficulty.shopCostsModifier);
		else
			return 0;
	}
}
