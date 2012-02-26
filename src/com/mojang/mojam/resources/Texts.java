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

	public String winCharacter(int team, int characterID) {
		String winMessage;
		if (team == Team.Team1) {
			winMessage = getStatic("gameplay.player1Win");
		} else {
			winMessage = getStatic("gameplay.player2Win");
		}
		return MessageFormat.format(winMessage, playerNameCharacter(characterID));
	}

	public String playerNameCharacter(int characterID) {
		return getStatic("gameplay.player" + (characterID + 1) + "Name");
	}

	public String hasDiedCharacter(int characterID) {
		return MessageFormat.format(getStatic("player.hasDied"), playerNameCharacter(characterID));
	}

	public String scoreCharacter(int characterID, int score) {
		return MessageFormat.format(getStatic("player.score"), playerNameCharacter(characterID), score);
	}

	public String cost(int cost) {
		return MessageFormat.format(getStatic("player.cost"), String.valueOf(cost));
	}

	public String health(float health, float maxHealth) {
		return MessageFormat.format(getStatic("player.health"), Math.floor(health / maxHealth * 100));
	}

	public String money(int money) {
		return MessageFormat.format(getStatic("player.money"), money);
	}

	public String FPS(int fps) {
		return MessageFormat.format(getStatic("gameplay.FPS"), fps);
	}

	public String latency(String ms) {
		return MessageFormat.format(getStatic("gameplay.latency"), ms);
	}

	public String nextLevel(int nextLevel) {
		return MessageFormat.format(getStatic("player.nextLevel"), nextLevel);
	}

	public String playerExp(int pexp) {
		return MessageFormat.format(getStatic("player.exp"), pexp);
	}

	public String playerLevel(int plevel) {
		return MessageFormat.format(getStatic("player.level"), plevel);
	}
	
	public String[] shopTooltipLines(String shopItemName) {
	    return new String[] { 
	            getStatic("shop." + shopItemName + "TooltipTitle"),
	            getStatic("shop." + shopItemName + "TooltipLine1"),
	            getStatic("shop." + shopItemName + "TooltipLine2"),
	    };
	}

	public String upgradeNotEnoughMoney(int cost) {
		return MessageFormat.format(getStatic("upgrade.notEnoughMoney"), cost);
	}

	public String upgradeTo(int upgradelevel) {
		return MessageFormat.format(getStatic("upgrade.to"), upgradelevel);
	}

	public String buildRail(int cost) {
		return MessageFormat.format(getStatic("build.rail"), cost);
	}

	public String buildDroid(int cost) {
		return MessageFormat.format(getStatic("build.droid"), cost);
	}

	public String removeRail(int cost) {
		return MessageFormat.format(getStatic("build.removeRail"), cost);
	}

}