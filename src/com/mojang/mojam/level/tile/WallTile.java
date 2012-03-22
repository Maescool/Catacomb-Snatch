package com.mojang.mojam.level.tile;

import java.util.List;

import com.mojang.mojam.entity.Entity;
import com.mojang.mojam.level.Level;
import com.mojang.mojam.math.BB;
import com.mojang.mojam.network.TurnSynchronizer;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.AbstractBitmap;
import com.mojang.mojam.screen.AbstractScreen;

public class WallTile extends Tile {
	static final int WALLHEIGHT = 56;
	public static final int COLOR = 0xffff0000;
	private static final String NAME = "WALL";

	public WallTile() {
		img = TurnSynchronizer.synchedRandom.nextInt(Art.wallTileColors.length);
		minimapColor = Art.wallTileColors[img][0];
	}
	
	@Override
	public void init(Level level, int x, int y) {
		super.init(level, x, y);
	}

	@Override
	public boolean canPass(Entity e) {
		return false;
	}

	@Override
	public void addClipBBs(List<BB> list, Entity e) {
		if (canPass(e))
			return;

		list.add(new BB(this, x * Tile.WIDTH, y * Tile.HEIGHT - 6, (x + 1)
				* Tile.WIDTH, (y + 1) * Tile.HEIGHT));
	}

	@Override
	public void render(AbstractScreen screen) {
		screen.blit(Art.wallTiles[img][0], x * Tile.WIDTH, y * Tile.HEIGHT
				- (WALLHEIGHT - Tile.HEIGHT));
	}

	@Override
	public void renderTop(AbstractScreen screen) {
		screen.blit(Art.wallTiles[img][0], x * Tile.WIDTH, y * Tile.HEIGHT
				- (WALLHEIGHT - Tile.HEIGHT), 32, 32);
	}

	@Override
	public boolean isBuildable() {
		return false;
	}

	@Override
	public boolean castShadow() {
		return true;
	}

	@Override
	public int getColor() {
		return WallTile.COLOR;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return WallTile.NAME;
	}

	@Override
	public AbstractBitmap getBitMapForEditor() {
		return Art.wallTiles[0][0];
	}
	
	@Override
	public int getMiniMapColor() {
		return  minimapColor;
	}
}
