package com.mojang.mojam.entity.building;

import com.mojang.mojam.entity.Player;
import com.mojang.mojam.entity.weapon.Shotgun;
import com.mojang.mojam.screen.Art;

public class ShopItemShotgun extends ShopItem {

    public ShopItemShotgun(double x, double y, int team) {
        super("shotgun",x, y, team, 300, 5);
        setSprite(Art.rifle[0][0]);
        teamTooltipYOffset = (team == 2) ? 153 : -63; 
    }

    public void useAction(Player player) {
    	player.weapon = new Shotgun(player);
	}
}