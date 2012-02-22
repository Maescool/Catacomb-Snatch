package com.mojang.mojam.entity.shop;

import com.mojang.mojam.entity.Player;
import com.mojang.mojam.entity.building.ShopItem;
import com.mojang.mojam.entity.weapon.GrenadeLauncher;
import com.mojang.mojam.gui.Notifications;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Bitmap;

public class ShopGrenadeLauncher extends ShopItem {

	private static final int COST = 400;
	
	public ShopGrenadeLauncher(double x, double y, int team) {
		super(x, y, team);
	}
	
	@Override
	protected int getCost() {
		return COST;
	}
	
	@Override
	public Bitmap getSprite() {
		return Art.grenadeLauncher;
	}
	
	@Override
	protected void itemUsed(Player player) {
		player.weapon = new GrenadeLauncher(player);
	}
	
	@Override
	protected boolean tryUse(Player player) {
		if(player.plevel < 3) {
			Notifications.getInstance().add("This item requires at least level 3!");
			return false;
		}
		return true;
	}
}