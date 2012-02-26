package com.mojang.mojam.resources;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import com.mojang.mojam.Options;
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
			System.err.println("Missing text property {" + property + "}");
			return "{" + property + "}";
		}
	}

	public String player1Win() {

		return MessageFormat.format(getStatic("gameplay.player1Win"),
				getPlayer1Name());
	}

	public String player2Win() {
		return MessageFormat.format(getStatic("gameplay.player2Win"),
				getStatic("gameplay.player2Name").toUpperCase());
	}

	public String playerName(int team) {
		if (team == Team.Team1) {
			return getPlayer1Name();
		}
		return getStatic("gameplay.player2Name");
	}

	public String playerWin(int team) {
		if (team == Team.Team1) {
			return player1Win();
		}
		return player2Win();
	}

	public String hasDied(int team) {
		return MessageFormat.format(getStatic("player.hasDied"),
				playerName(team));
	}

	public String score(int team, int score) {
		return MessageFormat.format(getStatic("player.score"),
				playerName(team), score);
	}

	public String cost(int cost) {
		return MessageFormat.format(getStatic("player.cost"),
				String.valueOf(cost));
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

	public String getPlayer1Name() {
		if (Options.getAsBoolean(Options.ALTERNATIVE)) {
			return getStatic("gameplay.player1NameAlt").toUpperCase();
		}
		return getStatic("gameplay.player1Name").toUpperCase();
	}
}