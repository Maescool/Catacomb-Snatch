package com.mojang.mojam.level.tile;

import com.mojang.mojam.level.Level;
import com.mojang.mojam.network.TurnSynchronizer;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.AbstractBitmap;
import com.mojang.mojam.screen.AbstractScreen;

public class DestroyedWallTile extends Tile {
	public static final int COLOR = -1;
	public static final String NAME = "DESTR.WALL";
	
	public Tile parent;

	public DestroyedWallTile(Tile t) {
		img = TurnSynchronizer.synchedRandom.nextInt(4);
		minimapColor = Art.floorTileColors[img & 7][img / 8];
		parent = t;
	}
	public void init(Level level, int x, int y) {
		super.init(level, x, y);
	}

	@Override
	public void render(AbstractScreen screen) {
		//super.render(screen);
		AbstractBitmap overlayTile = (Art.destWalls[img][0]).copy();
	    AbstractBitmap floorTile = (Art.floorTiles[img & 7][img / 8]).copy();
	    addShadows(floorTile);
	    screen.blit(floorTile, x * Tile.WIDTH, y * Tile.HEIGHT);
	    screen.blit(overlayTile, x * Tile.WIDTH, y * Tile.HEIGHT);
	}
	
	private void addShadows(AbstractBitmap tile){
	    
	    if (isShadowed_north) {
	        tile.blit(Art.shadow_north, 0, 0);
	    } else {
	        if (isShadowed_north_east) {
	            tile.blit(Art.shadow_north_east, Tile.WIDTH - Art.shadow_east.getWidth(), 0);
	        } 
	        if (isShadowed_north_west) {
	            tile.blit(Art.shadow_north_west, 0, 0);
	        }
	    }
	    if (isShadowed_east) {
            tile.blit(Art.shadow_east, Tile.WIDTH - Art.shadow_east.getWidth() , 0);
        }
        if (isShadowed_west) {
            tile.blit(Art.shadow_west, 0, 0);
        }
	}
	
	public void neighbourChanged(Tile tile) {
		final Tile w = level.getTile(x - 1, y);
		final Tile n = level.getTile(x, y - 1);
		final Tile e = level.getTile(x + 1, y);
		final Tile nw = level.getTile(x - 1, y - 1);
		final Tile ne = level.getTile(x + 1, y - 1);
        
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
	}

	public int getColor() {
		return DestroyedWallTile.COLOR;
	}

	public String getName() {
		return NAME;
	}

	@Override
	public boolean isBuildable() {
		return true;
	}
	
	public AbstractBitmap getBitMapForEditor() {
		return Art.floorTiles[5][0];
	}
	
	@Override
	public int getMiniMapColor() {
		return minimapColor;
	}
	
	@Override
	public void updateShadows(){
        neighbourChanged(null);
    }	
}
