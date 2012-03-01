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
	@Override
	public void init(Level level, int x, int y) {
		super.init(level, x, y);
		neighbourChanged(null);
	}

	@Override
	public void render(Screen screen) {
		super.render(screen);
	}

	@Override
	public void neighbourChanged(Tile tile) {
	    
		final Tile w = level.getTile(x - 1, y);
		final Tile n = level.getTile(x, y - 1);
		final Tile s = level.getTile(x, y + 1);
		final Tile e = level.getTile(x + 1, y);
		final Tile nw = level.getTile(x - 1, y - 1);
		final Tile ne = level.getTile(x + 1, y - 1);
		final Tile sw = level.getTile(x - 1, y + 1);
		final Tile se = level.getTile(x + 1, y + 1);
        
        if (w != null && w.castShadow()){
            this.isShadowed_west = true;
        } else this.isShadowed_west = false;
		if (n != null && n.castShadow()){
		    this.isShadowed_north = true;
		} else this.isShadowed_north = false;
		if (e != null && e.castShadow()){
		    this.isShadowed_east = true;
		} else this.isShadowed_east = false;
		if (ne != null && ne.castShadow() && e != null && !e.castShadow() && !this.isShadowed_north && !this.isShadowed_east){
		    this.isShadowed_north_east = true;
		} else this.isShadowed_north_east = false;
		if (nw != null && nw.castShadow() && w != null && !w.castShadow() && !this.isShadowed_north && !this.isShadowed_west){
            this.isShadowed_north_west = true;
        } else this.isShadowed_north_west = false;
		if (this.isSandTile(nw)) {
			img = 4 + 8;
		} else if (this.isSandTile(ne)) {
			img = 5 + 8;
		} else if (this.isSandTile(sw)) {
			img = 6 + 8;
		} else if (this.isSandTile(se)) {
			img = 7 + 8;
		}
		if (this.isSandTile(n)) {
			img = 8;
		} else if (this.isSandTile(e)) {
			img = 1 + 8;
		} else if (this.isSandTile(s)) {
			img = 2 + 8;
		} else if (this.isSandTile(w)) {
			img = 3 + 8;
		}
		minimapColor = Art.floorTileColors[img & 7][img / 8];
	}

	@Override
	public boolean isBuildable() {
		return true;
	}
	

	@Override
	public int getColor() {
		return FloorTile.COLOR;
	}


	@Override
	public String getName() {
		return FloorTile.NAME;
	}


	@Override
	public Bitmap getBitMapForEditor() {
		return Art.floorTiles[0][0];
	}

	@Override
	public int getMiniMapColor() {
		return minimapColor;
	}
	
	@Override
	public void updateShadows(){
        neighbourChanged(null);
    }	
	
	public boolean isSandTile(Tile tile) {
		return (tile instanceof SandTile || tile instanceof UnpassableSandTile);
	}
}
