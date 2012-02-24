package com.mojang.mojam.resources;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import com.mojang.mojam.entity.mob.Team;

public class Texts {
	protected final ResourceBundle texts;

	public Texts(Locale locale) {
		texts = ResourceBundle.getBundle("translations/texts", locale);
	}
	
	public String getStatic(String property) {
	    if (texts.containsKey(property)) {
	        return texts.getString(property);
	    } else {
	    	System.err.println("Missing text property {"+property+"}");
	        return "{"+property+"}";
	    }
	}

	public String player1Win() {
		return MessageFormat.format(getStatic("player1Win"), getStatic("player1Name").toUpperCase());
	}

	public String player2Win() {
		return MessageFormat.format(getStatic("player2Win"), getStatic("player2Name").toUpperCase());
	}
	
	public String playerName(int team) {
		if(team == Team.Team1) {
			return getStatic("player1Name");
		}
		return getStatic("player2Name");
	}
	
	public String playerWin(int team) {
		if(team == Team.Team1) {
			return player1Win();
		}
		return player2Win();
	}

	public String hasDied(int team) {
		return MessageFormat.format(getStatic("hasDied"), playerName(team));
	}

	public String score(int team, int score) {
		return MessageFormat.format(getStatic("score"), playerName(team), score);
	}

	public String cost(int cost) {
		return MessageFormat.format(getStatic("cost"), String.valueOf(cost));
	}

	public String health(float health, float maxHealth) {
		return MessageFormat.format(getStatic("health"), Math.floor(health / maxHealth * 100));
	}

	public String money(int money) {
		return MessageFormat.format(getStatic("money"), money);
	}

	public String FPS(int fps) {
		return MessageFormat.format(getStatic("FPS"), fps);
	}

	public String nextLevel(int nextLevel) {
		return MessageFormat.format(getStatic("nextLevel"), nextLevel);
	}

	public String playerExp(int pexp) {
		return MessageFormat.format(getStatic("playerExp"), pexp);
	}

	public String playerLevel(int plevel) {
		return MessageFormat.format(getStatic("playerLevel"), plevel);
	}
	
	public String upgradeNotEnoughMoney(int cost) {
		return MessageFormat.format(getStatic("upgrade.notEnoughMoney"), cost);
	}
	
	public String upgradeTo(int upgradelevel) {
		return MessageFormat.format(getStatic("upgrade.to"), upgradelevel);
	}
}
