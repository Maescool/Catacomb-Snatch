package com.mojang.mojam.entity.shop;

import com.mojang.mojam.entity.building.Building;
import com.mojang.mojam.entity.building.ShopItem;
import com.mojang.mojam.entity.building.Turret;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Bitmap;

public class ShopTurret extends ShopItem {

	private static final int COST = 150;

	public ShopTurret(double x, double y, int team) {
		super(x, y, team);
	}

	@Override
	protected int getCost() {
		return COST;
	}
	
	@Override
	public Bitmap getSprite() {
		return Art.turret[facing][0];
	}

	protected void itemUsed(com.mojang.mojam.entity.Player player) {
		Building item = new Turret(pos.x, pos.y, team);
		level.addEntity(item);
		player.pickup(item);
	}
}