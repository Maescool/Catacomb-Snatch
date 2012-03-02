package com.mojang.mojam.entity.building;

import com.mojang.mojam.entity.Player;
import com.mojang.mojam.entity.weapon.Rifle;
import com.mojang.mojam.screen.Art;

public class ShopItemRifle extends ShopItem {

    public ShopItemRifle(double x, double y, int team) {
        super("rifle",x, y, team, 0, 5);
        setSprite(Art.rifle[0][0]);
    }

    public void useAction(Player player) {
    	player.weapon = new Rifle(player);
	}
}