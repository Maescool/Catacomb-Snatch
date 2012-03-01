package com.mojang.mojam.entity.building;

import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Bitmap;


public enum EnumShopItem {
	//Buildings
	TURRET("turret", 150, 10, Art.turret[0][0]), 
	HARVESTER("harvester", 300, 22, Art.harvester[0][0]), 
	BOMB("bomb", 500, 7, Art.bomb),
	
	//Weapons
	RIFLE("rifle", 0, 7, Art.turret[0][0]), 
	SHOTGUN("shotgun", 500, 7, Art.turret[0][0]), 
	RAYGUN("raygun", 800, 7, Art.turret[0][0]);
	
	
	private final int cost;
	private final int yOffset;
	private final String name;
	private final Bitmap art;
	
	/**
	 * @param name the name of the item should be the same as in the translation text
	 * @param cost the cost to buy
	 * @param yOffset the offset for the art used
	 * @param art the art shown when buying at the player base
	 */
    EnumShopItem(String item, int cost, int yOffset, Bitmap art) {
    	this.name = item;
        this.cost = cost;
        this.yOffset = yOffset;
        this.art = art;
    }
    
    public String getItemName() {
    	return name;
    }
    
    public int getCost() {
    	return cost;
    }
    
    public int getYOffset() {
    	return yOffset;
    }
    
    public Bitmap getSprite() {
    	return art;
    }
}