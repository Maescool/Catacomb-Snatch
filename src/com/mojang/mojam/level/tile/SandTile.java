package com.mojang.mojam.level.tile;

import com.mojang.mojam.level.Level;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Bitmap;
import com.mojang.mojam.screen.Screen;

public class SandTile extends Tile {
	public static final int COLOR = 0xffA8A800;
	private static final String NAME = "SAND";

	public SandTile() {
		img = 5;
		minimapColor = Art.floorTileColors[img & 7][img / 8];
	}
	public void init(Level level, int x, int y) {
		super.init(level, x, y);
	}

	public void render(Screen screen) {
		super.render(screen);
	}
	
	public void neighbourChanged(Tile tile) {
        final Tile w = level.getTile(x - 1, y);
        final Tile n = level.getTile(x, y - 1);
        final Tile e = level.getTile(x + 1, y);
        final Tile nw = level.getTile(x - 1, y - 1);
        final Tile ne = level.getTile(x + 1, y - 1);
        
        if (w != null && w.castShadow()){
            this.isShadowed_west = true;
        }
        if (n != null && n.castShadow()){
            this.isShadowed_north = true;
        }
        if (e != null && e.castShadow()){
            this.isShadowed_east = true;
        }
        if (ne != null && ne.castShadow() && !this.isShadowed_north && !this.isShadowed_east){
            this.isShadowed_north_east = true;
        }
        if (nw != null && nw.castShadow() && !this.isShadowed_north && !this.isShadowed_west){
            this.isShadowed_north_west = true;
        }
	}

	public int getColor() {
		return SandTile.COLOR;
	}

	public String getName() {
		return NAME;
	}

	@Override
	public Bitmap getBitMapForEditor() {
		return Art.floorTiles[5][0];
	}
	
	@Override
	public int getMiniMapColor() {
		return minimapColor;
	}
}
