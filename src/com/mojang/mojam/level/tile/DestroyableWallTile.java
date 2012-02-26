package com.mojang.mojam.level.tile;

import java.util.List;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.Options;
import com.mojang.mojam.entity.Bullet;
import com.mojang.mojam.entity.Entity;
import com.mojang.mojam.entity.animation.LargeBombExplodeAnimation;
import com.mojang.mojam.entity.animation.TileExplodeAnimation;
import com.mojang.mojam.gui.TitleMenu;
import com.mojang.mojam.level.Level;
import com.mojang.mojam.math.BB;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Screen;

public class DestroyableWallTile extends WallTile {
	static final int WALLHEIGHT = 56;
	protected float maxHealth = 350;
	protected float health = maxHealth;
	protected int healthBarOffset = 10;

	public void init(Level level, int x, int y) {
		super.init(level, x, y);
		minimapColor = Art.wallTileColors[img % 3][0];
	  if (TitleMenu.difficulty != null) {
	    maxHealth = maxHealth + (float)((TitleMenu.difficulty.difficultyID + 1) * 50); 
	  }
	  health = maxHealth;
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
    int start = (int) ((health * 21 ) / maxHealth);
    
    screen.blit(Art.healthBar[start][0], x * Tile.WIDTH - 1, (y * Tile.HEIGHT) + Tile.HEIGHT - healthBarOffset);
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
	
	public void hurt(float damage) {
		float damageg = 0;
		if (MojamComponent.instance.player.plevel >= 3) {
			damageg = (damage / 4)*3;
		}
		if (MojamComponent.instance.player.plevel >= 5) {
			damageg = damage;
		}
		health -= damageg;
		if (health < 1) {
			MojamComponent.soundPlayer.playSound("/sound/Explosion 2.wav",
					(float) x * Tile.WIDTH, (float) y * Tile.HEIGHT);
			bomb(new LargeBombExplodeAnimation(x, y));
		}
	}
	
	public void handleCollision(Entity entity, double xa, double ya) {
		if (entity instanceof Bullet) {
		  ((Bullet)entity).handleCollision(this, xa, ya);
		}
	}

}
