package com.mojang.mojam.entity.shop;

import com.mojang.mojam.entity.Player;
import com.mojang.mojam.entity.building.Building;
import com.mojang.mojam.entity.building.Harvester;
import com.mojang.mojam.entity.building.ShopItem;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Bitmap;

public class ShopHarvester extends ShopItem {

	private static final int COST = 300;

	public ShopHarvester(double x, double y, int team) {
		super(x, y, team);
	}

	@Override
	protected int getCost() {
		return COST;
	}
	
	@Override
	public Bitmap getSprite() {
		return Art.harvester[facing][0];
	}

	@Override
	protected void itemUsed(Player player) {
		Building item = new Harvester(pos.x, pos.y, team);
		level.addEntity(item);
		player.pickup(item);
	}
}