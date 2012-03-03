package com.mojang.mojam.entity.building;

import com.mojang.mojam.entity.Player;
import com.mojang.mojam.entity.weapon.Raygun;
import com.mojang.mojam.screen.Art;

public class ShopItemRaygun extends ShopItem {

    public ShopItemRaygun(double x, double y, int team) {
        super("raygun",x, y, team, 800, 5);
        setSprite(Art.rifle[0][0]);
        teamTooltipYOffset = (team == 2) ? 122 : -32; 
    }

    public void useAction(Player player) {
    	player.weapon = new Raygun(player);
	}
}