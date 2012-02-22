package com.mojang.mojam.entity.shop;

import com.mojang.mojam.entity.Player;
import com.mojang.mojam.entity.building.Bomb;
import com.mojang.mojam.entity.building.Building;
import com.mojang.mojam.entity.building.ShopItem;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Bitmap;

public class ShopBomb extends ShopItem {

	private static final int COST = 500;

	public ShopBomb(double x, double y, int team) {
		super(x, y, team);
	}

	@Override
	protected int getCost() {
		return COST;
	}
	
	@Override
	public Bitmap getSprite() {
		return Art.bomb;
	}

	@Override
	protected void itemUsed(Player player) {
		Building item = new Bomb(pos.x, pos.y);
		level.addEntity(item);
		player.pickup(item);
	}
}