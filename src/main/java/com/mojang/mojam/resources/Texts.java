package com.mojang.mojam.resources;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Properties;

import com.mojang.mojam.GameCharacter;
import com.mojang.mojam.entity.mob.Team;

public class Texts {
	protected final Properties texts;
	protected final Properties fallbackTexts;

	public Texts(Locale locale) {
		InputStream stream;
		fallbackTexts = new Properties();
		
		texts = new Properties();
		try {
			stream = this.getClass().getResourceAsStream("/translations/texts_"+locale.getLanguage()+".txt");
			texts.load(new InputStreamReader(stream, "UTF8"));
			stream.close();
		} catch (Exception e) {
		}
		
		try {
			stream = this.getClass().getResourceAsStream("/translations/texts.txt");
			fallbackTexts.load(new InputStreamReader(stream, "UTF8"));
			stream.close();
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	public String getStatic(String property) {
		if (texts != null && texts.containsKey(property)) {
			return texts.getProperty(property);
		} else if (fallbackTexts != null && fallbackTexts.containsKey(property)) {
			return fallbackTexts.getProperty(property);
		} else {
			return "{"+property+"}";
		}
	}

	public String winCharacter(int team, GameCharacter character) {
		String winMessage;
		if (team == Team.Team1) {
			winMessage = getStatic("gameplay.player1Win");
		} else {
			winMessage = getStatic("gameplay.player2Win");
		}
		return MessageFormat.format(winMessage, playerNameCharacter(character));
	}

	public String playerNameCharacter(GameCharacter character) {
		return getStatic("gameplay.player" + (character.ordinal() + 1) + "Name");
	}

	public String hasDiedCharacter(GameCharacter character) {
		return MessageFormat.format(getStatic("player.hasDied"), playerNameCharacter(character));
	}

	public String scoreCharacter(GameCharacter character, int score) {
		return MessageFormat.format(getStatic("player.score"), playerNameCharacter(character), score);
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

	public String latency(int ms) {
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
	
	public String playerWeaponSlot(int slot) {
		return MessageFormat.format(getStatic("player.weaponSlot"), slot);
	}
	
	public String keyWeaponSlot(int slot) {
		return MessageFormat.format(getStatic("keys.weaponSlot"), slot);
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

	public String getFormated(String key, String arug) {
		return MessageFormat.format(getStatic(key), arug);
	}

}