package com.mojang.mojam.level.tile;

import com.mojang.mojam.level.Level;
import com.mojang.mojam.math.Facing;
import com.mojang.mojam.network.TurnSynchronizer;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Bitmap;
import com.mojang.mojam.screen.Screen;

public class RailTile extends Tile {
	protected static final String NAME = "RAIL";

	private static final int COLOR = -1;

	Tile parent;

	public int numConnections = 0;
	private boolean[] connections = new boolean[4];

	// private boolean[] exits = new boolean[4];

	public RailTile(Tile parent) {
		this.parent = parent;
		minimapColor = Art.floorTileColors[4][1];
	}

	public void init(Level level, int x, int y) {
		parent.init(level, x, y);
		super.init(level, x, y);
		neighbourChanged(null);
		parent.neighbourChanged(null);
	}

	public void render(Screen screen) {
		parent.render(screen);
		screen.blit(Art.rails[img][0], x * Tile.WIDTH, y * Tile.HEIGHT - 6);
	}

	public boolean isBuildable() {
		return false;
	}

	public void neighbourChanged(Tile tile) {
		// We check for <null> since we use it from the constructor (instead of
		// redirecting)
		if (tile != null && !(tile instanceof RailTile))
			return;

		boolean n = connections[Facing.NORTH] = level.getTile(x, y - 1) instanceof RailTile;
		boolean s = connections[Facing.SOUTH] = level.getTile(x, y + 1) instanceof RailTile;
		boolean w = connections[Facing.WEST] = level.getTile(x - 1, y) instanceof RailTile;
		boolean e = connections[Facing.EAST] = level.getTile(x + 1, y) instanceof RailTile;

		int c = (n ? 1 : 0) + (s ? 1 : 0) + (w ? 1 : 0) + (e ? 1 : 0);
		if (c <= 1) {
			img = (n || s) ? 1 : 0; // default is horizontal
		} else if (c == 2) { // ...
			if (n && s)
				img = 1;
			else if (w && e)
				img = 0;
			else {
				img = n ? 4 : 2;
				img += e ? 0 : 1;
			}
		} else { // 3 or more -> turning disk
			img = 6;
		}
		numConnections = c;
	}

	public boolean isConnectedTo(int facing) {
		return connections[facing];
	}

	public int getCost() {
		return 50;
	}

	public int getRandomDirection() {
		return getRandomDirection(-1);
	}

	public int getRandomDirection(int except) {
		int connCount = 0;
		int[] tmp = new int[4];
		for (int i = 0; i < 4; ++i)
			if (i != except && connections[i]) {
				tmp[connCount++] = i;
			}
		return connCount > 0 ? tmp[TurnSynchronizer.synchedRandom
				.nextInt(connCount)] : -1;
	}

	public boolean remove() {
		level.setTile(x, y, parent);
		
		// trigger neighbours checks
		if ( connections[Facing.NORTH] ) ( (RailTile) level.getTile(x, y - 1) ).neighbourChanged( null );
		if ( connections[Facing.SOUTH] ) ( (RailTile) level.getTile(x, y + 1) ).neighbourChanged( null );
		if ( connections[Facing.WEST] ) ( (RailTile) level.getTile(x - 1, y) ).neighbourChanged( null );
		if ( connections[Facing.EAST] ) ( (RailTile) level.getTile(x + 1, y) ).neighbourChanged( null );
		
		return true;
	}

	public int getColor() {
		return RailTile.COLOR;
	}

	public String getName() {
		return RailTile.NAME;
	}

	@Override
	public Bitmap getBitMapForEditor() {
		return Art.rails[1][0];
	}
	
	@Override
	public int getMiniMapColor() {
		return minimapColor;
	}

}
