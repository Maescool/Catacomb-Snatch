package com.mojang.mojam.level.tile;

import java.util.List;

import com.mojang.mojam.entity.Entity;
import com.mojang.mojam.entity.animation.LargeBombExplodeAnimation;
import com.mojang.mojam.entity.animation.TileExplodeAnimation;
import com.mojang.mojam.level.Level;
import com.mojang.mojam.math.BB;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.AbstractBitmap;
import com.mojang.mojam.screen.AbstractScreen;

public class DestroyableWallTile extends WallTile {

	static final int WALLHEIGHT = 56;
	public static final int COLOR = 0xffff7777;

	@Override
	public void init(Level level, int x, int y) {
		super.init(level, x, y);
		minimapColor = Art.wallTileColors[img % 3][0];
	}

	@Override
	public boolean canPass(Entity e) {
		return false;
	}

	@Override
	public void addClipBBs(List<BB> list, Entity e) {
		if (canPass(e)) {
			return;
		}

		list.add(new BB(this, x * Tile.WIDTH, y * Tile.HEIGHT - 6, (x + 1)
			* Tile.WIDTH, (y + 1) * Tile.HEIGHT));
	}

	@Override
	public void render(AbstractScreen screen) {
		screen.blit(Art.treasureTiles[4][0], x * Tile.WIDTH, y * Tile.HEIGHT
			- (WALLHEIGHT - Tile.HEIGHT));
	}

	@Override
	public void renderTop(AbstractScreen screen) {
		screen.blit(Art.treasureTiles[4][0], x * Tile.WIDTH, y * Tile.HEIGHT
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
	public void bomb(LargeBombExplodeAnimation largeBombExplodeAnimation) {
		level.setTile(x, y, new FloorTile());
		level.addEntity(new TileExplodeAnimation((x + 0.5) * Tile.WIDTH,
			(y + 0.5) * Tile.HEIGHT));
	}

	@Override
	public AbstractBitmap getBitMapForEditor() {
		return Art.treasureTiles[4][0];
	}

	@Override
	public String getName() {
		return "B.WALL";
	}

	@Override
	public int getColor() {
		return DestroyableWallTile.COLOR;
	}

	@Override
	public int getMiniMapColor() {
		return minimapColor;
	}
}
