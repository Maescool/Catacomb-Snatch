package com.mojang.mojam.entity.animation;

import com.mojang.mojam.level.tile.HoleTile;
import com.mojang.mojam.level.tile.Tile;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Screen;

public class PlayerFallingAnimation extends Animation {
    int characterID;
    
    public PlayerFallingAnimation(double x, double y, int characterID) {
        super(x, y, 60);
        this.characterID = characterID;
    }

    public void render(Screen screen) {
        
        int anim = life * 8 * 2 / duration;
        
        screen.blit(Art.getPlayer(characterID)[0][anim%8 + 8], pos.x, pos.y + Tile.HEIGHT - anim*3);
        screen.blit(Art.exclamation_mark, pos.x + 20, pos.y + Tile.HEIGHT - anim*3 - 5);
        Tile tileBelow = level.getTile((int)pos.x/Tile.WIDTH, (int)pos.y/Tile.WIDTH+1);
        if (tileBelow.getName() != HoleTile.NAME) tileBelow.render(screen);
    }
}
