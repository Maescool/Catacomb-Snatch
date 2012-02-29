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
		img=3;
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
		int index = 0;
		if (w != null && w.castShadow())
			index |= 0x4;
		if (n != null && n.castShadow())
			index |= 0x2;
		if (e != null && e.castShadow())
			index |= 0x1;

		if (n instanceof SandTile) {
			img = 4 + 8;
			index = 0;
		}
		if (s instanceof SandTile) {
			img = 5 + 8;
			index = 0;
		}

		if (index > 0) {
		    // If shadows are cast on this tile...
			int imageIndex = shadowImages[index];
			if (imageIndex < 4) // Row 1, first 4 columns
				img = Art.floorTiles.length + (imageIndex & 3);
			else
				// Row 2, first 4 columns
				img = 2 * Art.floorTiles.length + (imageIndex & 3);
		} else {
		    // No shadows are cast, it's a plain floor tile
		    if ((img >= 8 && img <= 11) || (img >= 16 && img <= 18)) {
		        // This tile currently has shadows
	        }
		    else if (TurnSynchronizer.synchedRandom != null) {
		            // Make this a random floor tile with no shadows
		            img = TurnSynchronizer.synchedRandom.nextInt(4);
	        } else {
	            // Be defensive! If we got here, then somehow we got called in the render() phase, which 
	            // probably should never happen.
	            //
	            // Give a warning, and carry on with a non-random number. This may put multiplayer
	            // games out of sync, but it's better than a NullPointerException crash.
	            //
	            // This warning can be removed if we don't see a problem. I haven't seen this warning yet
	            // in my testing, but problems have happened before when adding use of synchedRandom into methods
	            // where it previously wasn't used, so I'm being cautious.
	            System.err.println("WARNING: Averted crash in FloorTile#neighbourChanged(); synchedRandom is null");
	            System.err.println("         This should not happen, it means we got called during render phase.");
	            System.err.println("You may experience sync problems in multiplayer.");
	            img = 3;
	        }
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
