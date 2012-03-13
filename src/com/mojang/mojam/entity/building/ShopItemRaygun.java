package com.mojang.mojam.entity.building;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.entity.Player;
import com.mojang.mojam.entity.weapon.Raygun;
import com.mojang.mojam.gui.Notifications;
import com.mojang.mojam.screen.Art;

public class ShopItemRaygun extends ShopItem {

    public ShopItemRaygun(double x, double y, int team) {
        super("raygun",x, y, team, 800, 5);
        setSprite(Art.weaponList[2][0]);
        teamTooltipYOffset = (team == 2) ? 122 : -32; 
    }

    public void useAction(Player player) {
    	if(!player.weaponInventory.add(new Raygun(player))) {
        	if(this.team == MojamComponent.localTeam) {
                Notifications.getInstance().add(MojamComponent.texts.getStatic("gameplay.weaponAlready"));
        	}
    	}
	}
    
    @Override
	public boolean canBuy(Player player) {
		boolean alreadyOwned = player.weaponInventory.hasWeapon(new Raygun(player));
		if( alreadyOwned && this.team == MojamComponent.localTeam ) {
            Notifications.getInstance().add(MojamComponent.texts.getStatic("gameplay.weaponAlready"));
    	}
		return !alreadyOwned;
	}
}