package com.mojang.mojam.resources;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public class Texts {
	protected final ResourceBundle texts;

	public Texts(Locale locale) {
		texts = ResourceBundle.getBundle("properties/texts", locale);
	}

	public String playerName(int team) {
		switch (team) {
		case 1:
			return player1Name();
		case 2:
			return player2Name();
		}
		return "";
	}
	
	public String getStatic(String property) {
		return texts.getString(property);
	}

	public String player1Name() {
		return texts.getString("player1Name");
	}

	public String player2Name() {
		return texts.getString("player2Name");
	}

	public String player1Win() {
		return MessageFormat.format(texts.getString("player1Win"),
				player1Name().toUpperCase());
	}

	public String player2Win() {
		return MessageFormat.format(texts.getString("player2Win"),
				player2Name().toUpperCase());
	}

	public String hasDied(int team) {
		return MessageFormat.format(texts.getString("hasDied"),
				playerName(team));
	}

	public String score(int team, int score) {
		return MessageFormat.format(texts.getString("score"), playerName(team),
				score);
	}

	public String cost(int cost) {
		return MessageFormat.format(texts.getString("cost"), String.valueOf(cost));
	}

	public String health(float health, float maxHealth) {
		return MessageFormat.format(texts.getString("health"), health / maxHealth * 100);
	}

	public String money(int money) {
		return MessageFormat.format(texts.getString("money"), money);
	}

	public String waitingForClient() {
		return texts.getString("waitingForClient");
	}

	public String enterIP() {
		return texts.getString("enterIP");
	}

	public String FPS(int fps) {
		return MessageFormat.format(texts.getString("FPS"), fps);
	}

	public String nextLevel(int nextLevel) {
		return MessageFormat.format(texts.getString("nextLevel"), nextLevel);
	}

	public String playerExp(int pexp) {
		return MessageFormat.format(texts.getString("playerExp"), pexp);
	}

	public String playerLevel(int plevel) {
		return MessageFormat.format(texts.getString("playerLevel"), plevel);
	}

}
