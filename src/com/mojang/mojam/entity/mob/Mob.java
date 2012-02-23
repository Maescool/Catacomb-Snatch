package com.mojang.mojam.entity.mob;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.entity.*;
import com.mojang.mojam.entity.animation.EnemyDieAnimation;
import com.mojang.mojam.entity.building.SpawnerEntity;
import com.mojang.mojam.entity.loot.Loot;
import com.mojang.mojam.math.Vec2;
import com.mojang.mojam.level.tile.Tile;
import com.mojang.mojam.screen.*;

public abstract class Mob extends Entity {

	public final static double CARRYSPEEDMOD = 1.2;
	public final static int MoveControlFlag = 1;

	// private double speed = 0.82;
	private double speed = 1.0;
	protected int team;
	protected boolean doShowHealthBar = true;
    protected int healthBarOffset = 10;
	double dir = 0;
	public int hurtTime = 0;
	public int freezeTime = 0;
	public int bounceWallTime = 0;
	public float maxHealth = 10;
	public float health = maxHealth;
	public boolean isImmortal = false;
	public double xBump, yBump;
	public Mob carrying = null;
	public int yOffs = 8;
	public double xSlide;
	public double ySlide;
	public int deathPoints = 0;

	public Mob(double x, double y, int team) {
		super();
		setPos(x, y);
		this.team = team;
	}

	public void init() {
		super.init();
	}

	public void setStartHealth(float newHealth) {
		maxHealth = health = newHealth;
	}

	public double getSpeed() {
		return carrying != null ? speed * CARRYSPEEDMOD : speed;
	}

	public void deltaMove(Vec2 v) {
		super.move(v.x, v.y);
	}

	public int getTeam() {
		return team;
	}

	public boolean isEnemyOf(Mob m) {
		if (team == Team.Neutral || m.team == Team.Neutral)
			return false;
		return team != m.team;
	}

	public boolean isNotFriendOf(Mob m) {
		return team != m.team;
	}

	public void tick() {
		if (hurtTime > 0) {
			hurtTime--;
		}
		if (bounceWallTime > 0) {
			bounceWallTime--;
		}

		if (freezeTime > 0) {
			slideMove(xSlide, ySlide);
			xSlide *= 0.8;
			ySlide *= 0.8;

			if (xBump != 0 || yBump != 0) {
				move(xBump, yBump);
			}
			freezeTime--;
			return;
		} else {
			xSlide = ySlide = 0;
			if (health <= 0) {
				die();
				remove();
				return;
			}
		}
	}

	public void slideMove(double xa, double ya) {
		move(xa, ya);
	}

	public void die() {
		int particles = 8;
		//
		// for (int i = 0; i < particles; i++) {
		// double dir = i * Math.PI * 2 / particles;
		// level.addEntity(new Particle(pos.x, pos.y, Math.cos(dir),
		// Math.sin(dir)));
		// }

		if (getDeathPoints() > 0) {
			int loots = 4;
			for (int i = 0; i < loots; i++) {
				double dir = i * Math.PI * 2 / particles;

				level.addEntity(new Loot(pos.x, pos.y, Math.cos(dir), Math.sin(dir), getDeathPoints()));
			}
		}

		level.addEntity(new EnemyDieAnimation(pos.x, pos.y));

		MojamComponent.soundPlayer.playSound(getDeatchSound(), (float) pos.x, (float) pos.y);
	}

	public String getDeatchSound() {
		return "/sound/Explosion.wav";
	}

	public boolean shouldBounceOffWall(double xd, double yd) {
		if (bounceWallTime > 0)
			return false;
		Tile nextTile = level.getTile((int) (pos.x / Tile.WIDTH + Math.signum(xd)), (int) (pos.y / Tile.HEIGHT + Math.signum(yd)));
		boolean re = (nextTile != null && !nextTile.canPass(this));
		if (re)
			bounceWallTime = 10;
		return re;
	}

	public void render(Screen screen) {
		Bitmap image = getSprite();
		if (hurtTime > 0) {
			if (hurtTime > 40 - 6 && hurtTime / 2 % 2 == 0) {
				screen.colorBlit(image, pos.x - image.w / 2, pos.y - image.h / 2 - yOffs, 0xa0ffffff);
			} else {
				if (health < 0)
					health = 0;
				int col = (int) (180 - health * 180 / maxHealth);
				if (hurtTime < 10)
					col = col * hurtTime / 10;
				screen.colorBlit(image, pos.x - image.w / 2, pos.y - image.h / 2 - yOffs, (col << 24) + 255 * 65536);
			}
		} else {
			screen.blit(image, pos.x - image.w / 2, pos.y - image.h / 2 - yOffs);
		}

		if (doShowHealthBar && health < maxHealth) {
            addHealthBar(screen);
        }

		// @todo maybe not have the rendering of carried item here..
		renderCarrying(screen, 0);
	}

	protected void addHealthBar(Screen screen) {
        
        int start = (int) (health * 21 / maxHealth);
        
        screen.blit(Art.healthBar[start][0], pos.x - 16, pos.y + healthBarOffset);
    }

	protected void renderCarrying(Screen screen, int yOffs) {
		if (carrying == null)
			return;
		Bitmap image = carrying.getSprite();
		screen.blit(image, carrying.pos.x - image.w / 2, carrying.pos.y - image.h + 8 + yOffs);// image.h
		// / 2 - 8);
	}

	public abstract Bitmap getSprite();

	public void hurt(Entity source, float damage) {
		if (isImmortal)
			return;

		if (freezeTime <= 0) {
			
			if (source instanceof Bullet && !(this instanceof SpawnerEntity) && !(this instanceof RailDroid)) {
				Bullet bullet = (Bullet) source;
				if (bullet.owner instanceof Player) {
					Player pl = (Player) bullet.owner;
					pl.pexp++;
				}
			}
			
			hurtTime = 40;
			freezeTime = 5;
			health -= damage;
			if (health < 0) {
				health = 0;
			}

			double dist = source.pos.dist(pos);
			xBump = (pos.x - source.pos.x) / dist * 2;
			yBump = (pos.y - source.pos.y) / dist * 2;
		}
	}

	@Override
	public void collide(Entity entity, double xa, double ya) {
		xd += xa * 0.4;
		yd += ya * 0.4;
	}

	public int getDeathPoints() {
		return deathPoints;
	}

	public void onPickup() {
	}
}
