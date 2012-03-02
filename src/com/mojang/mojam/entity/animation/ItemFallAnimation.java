package com.mojang.mojam.entity.animation;

import com.mojang.mojam.level.tile.HoleTile;
import com.mojang.mojam.level.tile.Tile;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Bitmap;
import com.mojang.mojam.screen.Screen;

public class ItemFallAnimation extends Animation {
    Bitmap fallingImage;
    boolean isHarvester = false;
    
    public ItemFallAnimation(double x, double y, Bitmap fallingImage) {
        super(x, y, 60); // @random
        this.fallingImage = fallingImage;
    }

    public void render(Screen screen) {
   
        int anim;
        anim = life * 12 / duration;
        double posY = pos.y;
        if (!isHarvester) posY += Tile.HEIGHT;
        screen.blit(fallingImage, pos.x, posY - anim*3);
        
        Tile tileBelow = level.getTile((int)pos.x/Tile.WIDTH, (int)pos.y/Tile.WIDTH+1);
        if (tileBelow.getName() != HoleTile.NAME) tileBelow.render(screen);
    }
    
    public void setHarvester(){
        isHarvester = true;
    }
}
