package com.mojang.mojam.level.tile;

import com.mojang.mojam.level.Level;
import com.mojang.mojam.network.TurnSynchronizer;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Bitmap;
import com.mojang.mojam.screen.Screen;

public class FloorTile extends Tile {

	// Binary West|North|East
	static final int[] shadowImages = new int[] { -1, // 000
			3, // 001
			5, // 010
			6, // 011
			2, // 100
			0, // 101
			4, // 110
			1, // 111
	};
	public static final int COLOR = 0xffffffff;
	public static final String NAME = "FLOOR";

	public FloorTile() {
		img = TurnSynchronizer.synchedRandom.nextInt(4);
		minimapColor = Art.floorTileColors[img & 7][img / 8];
	}
	public void init(Level level, int x, int y) {
		super.init(level, x, y);
		neighbourChanged(null);
	}

	public void render(Screen screen) {
		super.render(screen);
	}

	public void neighbourChanged(Tile tile) {
		final Tile w = level.getTile(x - 1, y);
		final Tile n = level.getTile(x, y - 1);
		final Tile s = level.getTile(x, y + 1);
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
		if (ne != null && ne.castShadow() && e != null && !e.castShadow() && !this.isShadowed_north && !this.isShadowed_east){
		    this.isShadowed_north_east = true;
		}
		if (nw != null && nw.castShadow() && w != null && !w.castShadow() && !this.isShadowed_north && !this.isShadowed_west){
            this.isShadowed_north_west = true;
        }

		if (n instanceof SandTile) {
			img = 4 + 8;
		}
		if (s instanceof SandTile) {
			img = 5 + 8;
		}

		minimapColor = Art.floorTileColors[img & 7][img / 8];
	}

	public boolean isBuildable() {
		return true;
	}
	

	public int getColor() {
		return FloorTile.COLOR;
	}


	public String getName() {
		return FloorTile.NAME;
	}


	public Bitmap getBitMapForEditor() {
		return Art.floorTiles[0][0];
	}

	@Override
	public int getMiniMapColor() {
		return minimapColor;
	}
		
}
