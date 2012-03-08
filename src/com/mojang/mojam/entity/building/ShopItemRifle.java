package com.mojang.mojam.entity.building;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.entity.Player;
import com.mojang.mojam.entity.weapon.Rifle;
import com.mojang.mojam.entity.weapon.Shotgun;
import com.mojang.mojam.gui.Notifications;
import com.mojang.mojam.screen.Art;

public class ShopItemRifle extends ShopItem {

    public ShopItemRifle(double x, double y, int team) {
        super("rifle",x, y, team, 0, 5);
        setSprite(Art.weaponList[0][0]);
        teamTooltipYOffset = (team == 2) ? 185 : -95; 
        
    }

    
    public void useAction(Player player) {
    	if(!player.weaponInventory.add(new Rifle(player))) {
        	if(this.team == MojamComponent.localTeam) {
                Notifications.getInstance().add(MojamComponent.texts.getStatic("gameplay.weaponAlready"));
        	}
    	}
	}
    
    @Override
	public boolean canBuy(Player player) {
		boolean alreadyOwned = player.weaponInventory.hasWeapon(new Shotgun(player));
		if( alreadyOwned && this.team == MojamComponent.localTeam ) {
            Notifications.getInstance().add(MojamComponent.texts.getStatic("gameplay.weaponAlready"));
    	}
		return !alreadyOwned;
	}
}