package com.mojang.mojam.resources;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import com.mojang.mojam.Options;
import com.mojang.mojam.entity.mob.Team;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Bitmap;

public class Texts {
	protected final ResourceBundle texts;

	public Texts(Locale locale) {
		texts = ResourceBundle.getBundle("translations/texts", locale);
	}

	public String getStatic(String property) {
		if (texts.containsKey(property)) {
			return texts.getString(property);
		} else {
			System.err.println("Missing text property {" + property + "}");
			return "{" + property + "}";
		}
	}

	public String playerWin(int team, int characterID) {
		String winMessage;
		if (team == Team.Team1) {
			winMessage = getStatic("player1Win");
		}else{
			winMessage = getStatic("player2Win");
		}
		return MessageFormat.format(winMessage, getPlayerName(characterID));
	}

	public String hasDied(int characterID) {
		return MessageFormat.format(getStatic("player.hasDied"), getPlayerName(characterID));
	}

	public String score(int score, int characterID) {
		return MessageFormat.format(getStatic("player.score"), getPlayerName(characterID), score);
	}

	public String cost(int cost) {
		return MessageFormat.format(getStatic("player.cost"), String.valueOf(cost));
	}

	public String health(float health, float maxHealth) {
		return MessageFormat.format(getStatic("player.health"),
				Math.floor(health / maxHealth * 100));
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

	public String getPlayerName(int characterID) {
		return getStatic("gameplay.player" + (characterID + 1) + "Name").toUpperCase();
	}
}