package com.mojang.mojam.level.tile;

import java.util.List;

import com.mojang.mojam.entity.Entity;
import com.mojang.mojam.entity.animation.LargeBombExplodeAnimation;
import com.mojang.mojam.level.Level;
import com.mojang.mojam.math.BB;
import com.mojang.mojam.math.BBOwner;
import com.mojang.mojam.network.TurnSynchronizer;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Screen;

public class Tile implements BBOwner {
    public static final int HEIGHT = 32;
    public static final int WIDTH = 32;

    public Level level;
    public int x, y;
    public int img;
    public int minimapColor;

    public void init(Level level, int x, int y) {
        this.level = level;
        this.x = x;
        this.y = y;
        img = TurnSynchronizer.synchedRandom.nextInt(4);
        minimapColor = Art.floorTileColors[img & 7][img / 8];
    }

    public boolean canPass(Entity e) {
        return true;
    }

    public void render(Screen screen) {
        screen.blit(Art.floorTiles[img & 7][img / 8], x * Tile.WIDTH, y * Tile.HEIGHT);
    }

    public void addClipBBs(List<BB> list, Entity e) {
        if (canPass(e)) return;

        list.add(new BB(this, x * Tile.WIDTH, y * Tile.HEIGHT, (x + 1) * Tile.WIDTH, (y + 1) * Tile.HEIGHT));
    }

    public void handleCollision(Entity entity, double xa, double ya) {
    }

    public boolean isBuildable() {
        return false;
    }

    public void neighbourChanged(Tile tile) {
    }

    public int getCost() {
        return 0;
    }

    public boolean castShadow() {
        return false;
    }

    public void renderTop(Screen screen) {
    }

    public void bomb(LargeBombExplodeAnimation largeBombExplodeAnimation) {
    }
}