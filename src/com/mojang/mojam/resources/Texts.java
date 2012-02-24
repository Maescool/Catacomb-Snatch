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
		return MessageFormat.format(getStatic("gameplay.player1.Win"), getStatic("gameplay.player1.Name").toUpperCase());
	}

	public String player2Win() {
		return MessageFormat.format(getStatic("gameplay.player2.Win"), getStatic("gameplay.player2.Name").toUpperCase());
	}
	
	public String playerName(int team) {
		if(team == Team.Team1) {
			return getStatic("gameplay.player1.Name");
		}
		return getStatic("gameplay.player2.Name");
	}
	
	public String playerWin(int team) {
		if(team == Team.Team1) {
			return player1Win();
		}
		return player2Win();
	}

	public String hasDied(int team) {
		return MessageFormat.format(getStatic("player.hasDied"), playerName(team));
	}

	public String score(int team, int score) {
		return MessageFormat.format(getStatic("player.score"), playerName(team), score);
	}

	public String cost(int cost) {
		return MessageFormat.format(getStatic("building.cost"), String.valueOf(cost));
	}

	public String health(float health, float maxHealth) {
		return MessageFormat.format(getStatic("player.health"), Math.floor(health / maxHealth * 100));
	}

	public String money(int money) {
		return MessageFormat.format(getStatic("player.money"), money);
	}

	public String FPS(int fps) {
		return MessageFormat.format(getStatic("FPS"), fps);
	}

	public String nextLevel(int nextLevel) {
		return MessageFormat.format(getStatic("player.nextLevel"), nextLevel);
	}

	public String playerExp(int pexp) {
		return MessageFormat.format(getStatic("player.Exp"), pexp);
	}

	public String playerLevel(int plevel) {
		return MessageFormat.format(getStatic("player.Level"), plevel);
	}

}
