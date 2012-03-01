package com.mojang.mojam.level.tile;

import java.util.List;

import com.mojang.mojam.entity.Entity;
import com.mojang.mojam.entity.animation.LargeBombExplodeAnimation;
import com.mojang.mojam.level.IEditable;
import com.mojang.mojam.level.Level;
import com.mojang.mojam.math.BB;
import com.mojang.mojam.math.BBOwner;
import com.mojang.mojam.math.Facing;
import com.mojang.mojam.network.TurnSynchronizer;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Bitmap;
import com.mojang.mojam.screen.Screen;

public abstract class Tile implements BBOwner, IEditable {
	public static final int HEIGHT = 32;
	public static final int WIDTH = 32;

	public Level level;
	public int x, y;
	public int img = -1; // no image set yet
	public int minimapColor;

	public boolean isShadowed_north;
	public boolean isShadowed_east;
	public boolean isShadowed_west;
	public boolean isShadowed_north_east;
	public boolean isShadowed_north_west;
    
	public Tile() {
		if (img == -1) img = TurnSynchronizer.synchedRandom.nextInt(4);
		minimapColor = Art.floorTileColors[img & 7][img / 8];
	}

	public void init(Level level, int x, int y) {
		this.level = level;
		this.x = x;
		this.y = y;
	}

	public boolean canPass(Entity e) {
		return true;
	}

	public void render(Screen screen) {
	    
	    Bitmap floorTile = (Art.floorTiles[img & 7][img / 8]).copy();
	    addShadows(floorTile);
	    screen.blit(floorTile, x * Tile.WIDTH, y * Tile.HEIGHT);
	    
	    
	}
	
	private void addShadows(Bitmap tile){
	    
	    if (isShadowed_north) {
	        tile.blit(Art.shadow_north, 0, 0);
	    } else {
	        if (isShadowed_north_east) {
	            tile.blit(Art.shadow_north_east, Tile.WIDTH - Art.shadow_east.w, 0);
	        } 
	        if (isShadowed_north_west) {
	            tile.blit(Art.shadow_north_west, 0, 0);
	        }
	    }
	    if (isShadowed_east) {
            tile.blit(Art.shadow_east, Tile.WIDTH - Art.shadow_east.w , 0);
        }
        if (isShadowed_west) {
            tile.blit(Art.shadow_west, 0, 0);
        }
	}

	public void addClipBBs(List<BB> list, Entity e) {
		if (canPass(e))
			return;

		list.add(new BB(this, x * Tile.WIDTH, y * Tile.HEIGHT, (x + 1)
				* Tile.WIDTH, (y + 1) * Tile.HEIGHT));
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
	
	public void updateShadows(){
	}
}