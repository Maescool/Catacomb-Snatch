package com.mojang.mojam.level.tile;

import java.util.List;

import com.mojang.mojam.entity.Bullet;
import com.mojang.mojam.entity.Entity;
import com.mojang.mojam.entity.animation.LargeBombExplodeAnimation;
import com.mojang.mojam.entity.animation.TileExplodeAnimation;
import com.mojang.mojam.level.Level;
import com.mojang.mojam.math.BB;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Screen;

public class DestroyableWallTile extends WallTile {
	static final int WALLHEIGHT = 56;
	protected int maxHealth = 250;
	protected int health = maxHealth;
	protected int healthBarOffset = 10;

	public void init(Level level, int x, int y) {
		super.init(level, x, y);
		minimapColor = Art.wallTileColors[img % 3][0];
	}

	public boolean canPass(Entity e) {
		return false;
	}

	public void addClipBBs(List<BB> list, Entity e) {
		if (canPass(e))
			return;

		list.add(new BB(this, x * Tile.WIDTH, y * Tile.HEIGHT - 6, (x + 1)
				* Tile.WIDTH, (y + 1) * Tile.HEIGHT));
	}

	public void render(Screen screen) {
		screen.blit(Art.treasureTiles[4][0], x * Tile.WIDTH, y * Tile.HEIGHT
				- (WALLHEIGHT - Tile.HEIGHT));
		if (health < maxHealth) {
      addHealthBar(screen);
    }
	}
	
	protected void addHealthBar(Screen screen) {
    
    int start = (int) (health * 21 / maxHealth);
    
    screen.blit(Art.healthBar[start][0], x - 16, y + healthBarOffset);
  }

	public void renderTop(Screen screen) {
		screen.blit(Art.treasureTiles[4][0], x * Tile.WIDTH, y * Tile.HEIGHT
				- (WALLHEIGHT - Tile.HEIGHT), 32, 32);
	}

	public boolean isBuildable() {
		return false;
	}

	public boolean castShadow() {
		return true;
	}

	public void bomb(LargeBombExplodeAnimation largeBombExplodeAnimation) {
		level.setTile(x, y, new FloorTile());

		level.getTile(x, y).neighbourChanged(this);
		level.getTile(x - 1, y).neighbourChanged(this);
		level.getTile(x + 1, y).neighbourChanged(this);
		level.getTile(x, y - 1).neighbourChanged(this);
		level.getTile(x, y + 1).neighbourChanged(this);

		level.addEntity(new TileExplodeAnimation((x + 0.5) * Tile.WIDTH,
				(y + 0.5) * Tile.HEIGHT));
	}
	
	public void hurt() {
		if (health==0) {
			bomb(new LargeBombExplodeAnimation(x, y));
		}
		health--;
	}
	
	public void handleCollision(Entity entity, double xa, double ya) {
		try {
		  ((Bullet)entity).handleCollision(this, xa, ya);
		} catch (Exception e) {
			
		}
	}

}
