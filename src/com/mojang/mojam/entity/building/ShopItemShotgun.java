package com.mojang.mojam.entity.building;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.entity.Player;
import com.mojang.mojam.entity.weapon.Rifle;
import com.mojang.mojam.entity.weapon.Shotgun;
import com.mojang.mojam.gui.Notifications;
import com.mojang.mojam.screen.Art;

public class ShopItemShotgun extends ShopItem {

    public ShopItemShotgun(double x, double y, int team) {
        super("shotgun",x, y, team, 300, 5);
        setSprite(Art.weaponList[1][0]);
        teamTooltipYOffset = (team == 2) ? 153 : -63; 
    }

    public void useAction(Player player) {
    	if(!player.weaponInventory.add(new Shotgun(player))) {
        	if(this.team == MojamComponent.localTeam) {
                Notifications.getInstance().add(MojamComponent.texts.getStatic("gameplay.weaponAlready"));
        	}
    	}
	}
}