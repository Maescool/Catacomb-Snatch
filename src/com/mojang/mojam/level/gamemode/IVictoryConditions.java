package com.mojang.mojam.level.gamemode;

import com.mojang.mojam.level.Level;

public interface IVictoryConditions {
	void updateVictoryConditions(Level level);
	boolean isVictoryConditionAchieved();
	int playerVictorious();
}
