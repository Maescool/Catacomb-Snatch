package com.mojang.mojam.level.gamemode;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.level.Level;

public class GoldRush implements IVictoryConditions {
	
	private boolean bVictoryAchieved;
	private int winner;
	
	@Override
	public void updateVictoryConditions(Level level) {
		if(MojamComponent.instance.players[0] != null)
			level.player1Score = MojamComponent.instance.players[0].score;
		if(MojamComponent.instance.players[1] != null)
			level.player2Score = MojamComponent.instance.players[1].score;
		
		if (level.player1Score >= level.TARGET_SCORE) {
        	bVictoryAchieved = true;
        	winner = 1;
        }
        if (level.player2Score >= level.TARGET_SCORE) {
        	bVictoryAchieved = true;
        	winner = 2;
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
