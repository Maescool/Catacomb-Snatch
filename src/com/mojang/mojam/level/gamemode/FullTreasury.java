package com.mojang.mojam.level.gamemode;

import com.mojang.mojam.level.Level;

public class FullTreasury implements IVictoryConditions {
	
	private boolean bVictoryAchieved;
	private int winner;
	@Override
	public void updateVictoryConditions(Level level) {
		if (level != null) {
            if (level.player1Score >= level.TARGET_SCORE) {
            	bVictoryAchieved = true;
            	winner = 1;
            }
            if (level.player2Score >= level.TARGET_SCORE) {
            	bVictoryAchieved = true;
            	winner = 2;
            }
        }
	}

	@Override
	public boolean isVictoryConditionAchieved() {
		return bVictoryAchieved;
	}

	@Override
	public int playerVictorious() {
		return winner;
	}

}
