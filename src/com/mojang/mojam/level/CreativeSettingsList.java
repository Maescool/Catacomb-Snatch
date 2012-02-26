package com.mojang.mojam.level;


/**
 * Bundle all settings for the creative mode
 */
public class CreativeSettingsList {

	private final static String[] settings = {
		"creative.immortal", "creative.spawners", "creative.freebuilding",
		"creative.freeupgrade", "creative.weapondamage" };

	/**
	 * Get creative mode settings list
	 * 
	 * @return Settings list
	 */
	public static String[] getCreativeSettings() {
			
		return settings;
	}
}