package com.mojang.mojam.entity.animation;

import com.mojang.mojam.level.tile.HoleTile;
import com.mojang.mojam.level.tile.Tile;
import com.mojang.mojam.screen.AbstractBitmap;
import com.mojang.mojam.screen.AbstractScreen;

public class ItemFallAnimation extends Animation {
    AbstractBitmap fallingImage;
    boolean isHarvester = false;
    
    public ItemFallAnimation(double x, double y, AbstractBitmap fallingImage) {
        super(x, y, 60); // @random
        this.fallingImage = fallingImage;
    }

    public void render(AbstractScreen screen) {
   
        int anim;
        anim = life * 12 / duration;
        double posY = pos.y;
        if (!isHarvester) posY += Tile.HEIGHT;
        screen.blit(fallingImage,(int) pos.x, (int)(posY - anim*3));
        
        Tile tileBelow;
        for(int i = 1; i <= 2; i++) {
        	tileBelow = level.getTile((int)pos.x/Tile.WIDTH, (int)pos.y/Tile.WIDTH+i);
        	if (tileBelow.getName() != HoleTile.NAME) tileBelow.render(screen);
        }
    }
    
    public void setHarvester(){
        isHarvester = true;
    }
}
