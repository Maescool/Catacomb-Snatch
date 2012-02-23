package com.mojang.mojam.level.tile;

import com.mojang.mojam.level.Level;
import com.mojang.mojam.network.TurnSynchronizer;
import com.mojang.mojam.screen.Art;
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
			int imageIndex = shadowImages[index];
			if (imageIndex < 4) // Row 1, first 4 columns
				img = Art.floorTiles.length + (imageIndex & 3);
			else
				// Row 2, first 4 columns
				img = 2 * Art.floorTiles.length + (imageIndex & 3);
		}
		minimapColor = Art.floorTileColors[img & 7][img / 8];
	}

	public boolean isBuildable() {
		return true;
	}
}
