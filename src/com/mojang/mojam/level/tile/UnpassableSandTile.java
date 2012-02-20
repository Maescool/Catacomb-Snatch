package com.mojang.mojam.level.tile;

import com.mojang.mojam.entity.Entity;
import com.mojang.mojam.level.Level;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Screen;

public class UnpassableSandTile extends Tile {
    public void init(Level level, int x, int y) {
        super.init(level, x, y);
        img = 6;
        minimapColor = Art.floorTileColors[img & 7][img / 8];
    }

    public void render(Screen screen) {
        super.render(screen);
    }

    public boolean canPass(Entity e) {
        return false;
    }
}
