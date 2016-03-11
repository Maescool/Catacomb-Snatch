package com.mojang.mojam.entity.building;

import com.mojang.mojam.entity.Player;
import com.mojang.mojam.screen.Art;

public class ShopItemTurret extends ShopItem {

    public ShopItemTurret(double x, double y, int team) {
        super("turret",x, y, team, 150, 10);
        
        facing = 0;
        
        setSprite(Art.turret[facing][0]);
    }

    public void useAction(Player player) {
        Building item = new Turret(pos.x, pos.y, team);
        level.addEntity(item);
        player.pickup(item);
	}
    
    @Override
    public void tick() {
        super.tick();
        if(freezeTime > 0) return;
        setSprite(Art.turret[(facing++)&7][0]);
        freezeTime = 100;
    }

	@Override
	boolean canBuy(Player player) {
		return true;
	}
    
}
