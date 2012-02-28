package com.mojang.mojam.entity.building;


import java.awt.Color;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.mojang.mojam.entity.Bullet;
import com.mojang.mojam.entity.Player;
import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.Options;
import com.mojang.mojam.entity.Entity;
import com.mojang.mojam.entity.animation.SmokeAnimation;
import com.mojang.mojam.entity.building.Turret;
import com.mojang.mojam.entity.building.RailBomb;
import com.mojang.mojam.entity.loot.Loot;
import com.mojang.mojam.entity.loot.LootCollector;
import com.mojang.mojam.entity.mob.Mob;
import com.mojang.mojam.entity.mob.RailDroid;
import com.mojang.mojam.level.DifficultyInformation;
import com.mojang.mojam.level.tile.*;
import com.mojang.mojam.math.BB;
import com.mojang.mojam.math.Vec2;
import com.mojang.mojam.network.TurnSynchronizer;
import com.mojang.mojam.screen.*;

public class RailTurret extends Building {
	private enum Direction {
		UNKNOWN, LEFT, UP, RIGHT, DOWN;

		private Direction turnBy180DegreesRight() {
			switch (this) {
			case LEFT:
				return RIGHT;
			case UP:
				return DOWN;
			case RIGHT:
				return LEFT;
			case DOWN:
				return UP;
			case UNKNOWN:
			default:
				return UNKNOWN;
			}
		}
	}

	private static final float BULLET_DAMAGE = .75f;
	
	private Direction dir = Direction.UNKNOWN;
	private Direction lDir = Direction.DOWN;
	private int noTurnTime = 0;
	private int pauseTime = 0;
	public int team;
	public static boolean creative = Options.getAsBoolean(Options.CREATIVE);
	public static int cost = 250;
	
	private int delayTicks = 0;
	private int delay;
	public int radius;
	public int radiusSqr;

	private int[] upgradeRadius = new int[] { 3 * Tile.WIDTH, 5 * Tile.WIDTH, 7 * Tile.WIDTH };
	private int[] upgradeDelay = new int[] { 24, 21, 18 };

	private int facing = 0;
	
	public Bitmap areaBitmap;
	private static final int RADIUS_COLOR = new Color(240, 210, 190).getRGB();
	
	private Player owner;
	

	public RailTurret(double x, double y, int team, int upgradeLevel, Player owner) {
		super(x, y, team);
		this.team = team;
		this.setSize(10, 8);
		this.upgradeLevel = upgradeLevel;
		setStartHealth(10);
		speed = 0.7;
		upgradeComplete(upgradeLevel);
		
		//freezeTime = 10;
		this.owner = owner;
		
		if(creative)
			isImmortal = true;
		
	}
	
	@Override
	public void init() {
		makeUpgradeableWithCosts(new int[] { DifficultyInformation.calculateCosts(500), 
				DifficultyInformation.calculateCosts(1000), 
				DifficultyInformation.calculateCosts(5000)});
	}

	public void tick() {

		xBump = yBump = 0;

		super.tick();
		if (freezeTime > 0)
			return;
		if (delayTicks > 0)
			speed = 0.25;
		if (delayTicks <= 0)
			speed = 0.7;
		if (!isCarried() && !(--delayTicks > 0)) {
	    // find target
  		Set<Entity> entities = level.getEntities(pos.x - radius, pos.y - radius, pos.x + radius, pos.y + radius);
  
  		Entity closest = null;
  		double closestDist = 99999999.0f;
  		for (Entity e : entities) {
  			if (!(e instanceof Mob) || (e instanceof RailDroid && e.team == this.team) || ((e instanceof Bomb)?(((Bomb)e).lastCarrying == this.owner):false) || ((e instanceof RailBomb)?(((RailBomb)e).owner == this.owner):false))
  				continue;
  			if (!((Mob) e).isNotFriendOf(this))
  				continue;
  			final double dist = e.pos.distSqr(pos);
  			Bullet bullet = new Bullet(this, pos.x, pos.y, 0);
  			if (dist < radiusSqr && dist < closestDist && !isTargetBehindWall(e.pos.x, e.pos.y, bullet)) {
  				closestDist = dist;
  				closest = e;
  			}
  		}
  		if (closest != null) {
  		    // shoot
      		double invDist = 1.0 / Math.sqrt(closestDist);
      		double yd = closest.pos.y - pos.y;
      		double xd = closest.pos.x - pos.x;
      		double angle = (Math.atan2(yd, xd) + Math.PI * 1.625);
      		facing = (8 + (int) (angle / Math.PI * 4)) & 7;
      		Bullet bullet = new Bullet(this, xd * invDist, yd * invDist, BULLET_DAMAGE * ((upgradeLevel + 1) / 2.f));
      		bullet.pos.y -= 10;
      		level.addEntity(bullet);
      
      		if (upgradeLevel > 0) {
      			Bullet second_bullet = new Bullet(this, xd * invDist, yd * invDist, BULLET_DAMAGE * ((upgradeLevel + 1) / 2.f));
      			level.addEntity(second_bullet);
      			if (facing == 0 || facing == 4) {
      				bullet.pos.x -= 5;
      				second_bullet.pos.x += 5;
      			}
      		}
      
      		delayTicks = delay;
  		}
	}
		boolean hadPaused = pauseTime > 0;
		if (pauseTime > 0) {
			pauseTime--;
			if (pauseTime > 0)
				return;
		}
		int xt = (int) (pos.x / Tile.WIDTH);
		int yt = (int) (pos.y / Tile.HEIGHT);
		
		if (level.getTile(xt, yt) instanceof UnbreakableRailTile) ;

		boolean cr = level.getTile(xt, yt) instanceof RailTile;
		boolean lr = level.getTile(xt - 1, yt) instanceof RailTile;
		boolean rr = level.getTile(xt + 1, yt) instanceof RailTile;
		boolean ur = level.getTile(xt, yt - 1) instanceof RailTile;
		boolean dr = level.getTile(xt, yt + 1) instanceof RailTile;
		xd *= 0.4;
		yd *= 0.4;
		

		double xcd = pos.x - (xt * Tile.WIDTH + 16);
		double ycd = pos.y - (yt * Tile.HEIGHT + 16);
		boolean centerIsh = xcd * xcd + ycd * ycd < 2 * 2;
		boolean xcenterIsh = xcd * xcd < 2 * 2;
		boolean ycenterIsh = ycd * ycd < 2 * 2;

		if (!xcenterIsh)
			ur = false;
		if (!xcenterIsh)
			dr = false;

		if (!ycenterIsh)
			lr = false;
		if (!ycenterIsh)
			rr = false;

		int lWeight = 0;
		int uWeight = 0;
		int rWeight = 0;
		int dWeight = 0;

		if (noTurnTime > 0)
			noTurnTime--;

		if (noTurnTime == 0 && (!cr || dir == Direction.UNKNOWN || centerIsh)) {
			noTurnTime = 4;
			// int nd = 0;
			if (dir == Direction.LEFT && ur)
				uWeight += 16;
			if (dir == Direction.UP && rr)
				rWeight += 16;
			if (dir == Direction.RIGHT && dr)
				dWeight += 16;
			if (dir == Direction.DOWN && lr)
				lWeight += 16;

			if (lWeight + uWeight + rWeight + dWeight == 0) {
				if (dir == Direction.LEFT && lr)
					lWeight += 16;
				if (dir == Direction.UP && ur)
					uWeight += 16;
				if (dir == Direction.RIGHT && rr)
					rWeight += 16;
				if (dir == Direction.DOWN && dr)
					dWeight += 16;
			}

			if (lWeight + uWeight + rWeight + dWeight == 0) {
				if ((dir == Direction.LEFT || dir == Direction.RIGHT)) {
					if (ur)
						uWeight += 4;
					if (dr)
						dWeight += 4;
				}
				if ((dir == Direction.UP || dir == Direction.DOWN)) {
					if (lr)
						lWeight += 4;
					if (rr)
						rWeight += 4;
				}
			}
			if (lWeight + uWeight + rWeight + dWeight == 0) {
				if (lr)
					lWeight += 1;
				if (ur)
					uWeight += 1;
				if (rr)
					rWeight += 1;
				if (dr)
					dWeight += 1;
			}

			if (dir == Direction.LEFT)
				rWeight = 0;
			if (dir == Direction.UP)
				dWeight = 0;
			if (dir == Direction.RIGHT)
				lWeight = 0;
			if (dir == Direction.DOWN)
				uWeight = 0;

			int totalWeight = lWeight + uWeight + rWeight + dWeight;
			if (totalWeight == 0) {
				dir = Direction.UNKNOWN;
			} else {
				int res = TurnSynchronizer.synchedRandom.nextInt(totalWeight);
				// dir = 0;
				dir = dir.turnBy180DegreesRight();

				uWeight += lWeight;
				rWeight += uWeight;
				dWeight += rWeight;

				if (res < lWeight)
					dir = Direction.LEFT;
				else if (res < uWeight)
					dir = Direction.UP;
				else if (res < rWeight)
					dir = Direction.RIGHT;
				else if (res < dWeight)
					dir = Direction.DOWN;
			}

			// dir = nd;
		}

		if (cr) {
			double r = 1;
			if (!(dir == Direction.LEFT || dir == Direction.RIGHT)) {
				if (xcd < -r)
					xd += 0.3;
				if (xcd > +r)
					xd -= 0.3;
			}

			if (!(dir == Direction.UP || dir == Direction.DOWN)) {
				if (ycd < -r)
					yd += 0.3;
				if (ycd > +r)
					yd -= 0.3;
			}
			// if (!(dir == 1 || dir == 3) && xcd >= -r && xcd <= r) xd = -xcd;
			// if (!(dir == 2 || dir == 4) && ycd >= -r && ycd <= r) yd = -ycd;
		}
		//double speed = 0.7;
		if (dir != Direction.UNKNOWN)
			lDir = dir;
		if (dir == Direction.LEFT)
			xd -= speed;
		if (dir == Direction.UP)
			yd -= speed;
		if (dir == Direction.RIGHT)
			xd += speed;
		if (dir == Direction.DOWN)
			yd += speed;

		Vec2 oldPos = pos.clone();
		move(xd, yd);
		if (dir != Direction.UNKNOWN && oldPos.distSqr(pos) < 0.1 * 0.1) {
			if (hadPaused) {
				dir = dir.turnBy180DegreesRight();
				noTurnTime = 0;
			} else {
				pauseTime = 10;
				noTurnTime = 0;
			}
		}
		
		int xt2 = (int) (pos.x / Tile.WIDTH);
		int yt2 = (int) (pos.y / Tile.HEIGHT);
		
		if (health <= 0) {
			die();
			this.remove();
			level.removeEntity(this);
			level.removeFromEntityMap(this);
		}
		
		// level.getTile(xt, yt)
	}
	
	@Override
	protected void upgradeComplete() {
		maxHealth += 10;
		health = maxHealth;
		delay = upgradeDelay[upgradeLevel];
		radius = upgradeRadius[upgradeLevel];
		radiusSqr = radius * radius;
		areaBitmap = Bitmap.rangeBitmap(radius,RADIUS_COLOR);
		if (upgradeLevel != 0) justDroppedTicks = 80; //show the radius for a brief time
	}
	
	protected void upgradeComplete(int level) {
		if (level==1)maxHealth += 10;
		if (level==2)maxHealth += 20;
		if (level==3)maxHealth += 30;
		health = maxHealth;
		delay = upgradeDelay[upgradeLevel];
		radius = upgradeRadius[upgradeLevel];
		radiusSqr = radius * radius;
		areaBitmap = Bitmap.rangeBitmap(radius,RADIUS_COLOR);
		if (upgradeLevel != 0) justDroppedTicks = 80; //show the radius for a brief time
	}

	@Override
	public Bitmap getSprite() {
		switch (upgradeLevel) {
		case 1:
			return Art.turret2[facing][0];
		case 2:
			return Art.turret3[facing][0];
		default:
			return Art.turret[facing][0];
		}
	}

	public void handleCollision(Entity entity, double xa, double ya) {
		super.handleCollision(entity, xa, ya);
		if (entity instanceof RailTurret) {
			RailTurret other = (RailTurret) entity;
			if (lDir == Direction.LEFT && other.pos.x > pos.x - 4)
				return;
			if (lDir == Direction.UP && other.pos.y > pos.y - 4)
				return;
			if (lDir == Direction.RIGHT && other.pos.x < pos.x + 4)
				return;
			if (lDir == Direction.DOWN && other.pos.y < pos.y + 4)
				return;

			if (other.lDir == Direction.LEFT && pos.x > other.pos.x - 4)
				return;
			if (other.lDir == Direction.UP && pos.y > other.pos.y - 4)
				return;
			if (other.lDir == Direction.RIGHT && pos.x < other.pos.x + 4)
				return;
			if (other.lDir == Direction.DOWN && pos.y < other.pos.y + 4)
				return;
		}
	}

	@Override
	protected boolean shouldBlock(Entity e) {
		// if (e instanceof Player && ((Player) e).team == team) return false;
		if(e instanceof Bullet && ((Bullet) e).owner instanceof Turret && ((Bullet) e).owner.team == team) return false;
		return super.shouldBlock(e);
	}
	

/*	public void render(Screen screen) {
		super.render(screen);
		if (carrying) {
			screen.blit(Art.bullets[0][0], pos.x - 8, pos.y - 20 - yOffs);
		} else {
			screen.blit(Art.bullets[1][1], pos.x - 8, pos.y - 20 - yOffs);
		}
	}*/
	
	@Override
	public void render(Screen screen) {
		
		if((justDroppedTicks-- > 0 || highlight) && MojamComponent.localTeam==team) {
			drawRadius(screen);
	  }
		
		super.render(screen);
		
		if(health < maxHealth) {
			addHealthBar(screen);
		}
		
	}
	
	@Override
  public boolean move(double xa, double ya) {
		List<BB> bbs = level.getClipBBs(this);
		if (physicsSlide) {
			boolean moved = false;
			if (!removed)
				moved |= partMove(bbs, xa, 0);
			if (!removed)
				moved |= partMove(bbs, 0, ya);
			return moved;
		} else {
			boolean moved = true;
			if (!removed)
				moved &= partMove(bbs, xa, 0);
			if (!removed)
				moved &= partMove(bbs, 0, ya);
			return moved;
		}
	}
	
	private boolean partMove(List<BB> bbs, double xa, double ya) {
		double oxa = xa;
		double oya = ya;
		BB from = getBB();

		BB closest = null;
		double epsilon = 0.01;
		for (int i = 0; i < bbs.size(); i++) {
			BB to = bbs.get(i);
			if (from.intersects(to))
				continue;

			if (ya == 0) {
				if (to.y0 >= from.y1 || to.y1 <= from.y0)
					continue;
				if (xa > 0) {
					double xrd = to.x0 - from.x1;
					if (xrd >= 0 && xa > xrd) {
						closest = to;
						xa = xrd - epsilon;
						if (xa < 0)
							xa = 0;
					}
				} else if (xa < 0) {
					double xld = to.x1 - from.x0;
					if (xld <= 0 && xa < xld) {
						closest = to;
						xa = xld + epsilon;
						if (xa > 0)
							xa = 0;
					}
				}
			}

			if (xa == 0) {
				if (to.x0 >= from.x1 || to.x1 <= from.x0)
					continue;
				if (ya > 0) {
					double yrd = to.y0 - from.y1;
					if (yrd >= 0 && ya > yrd) {
						closest = to;
						ya = yrd - epsilon;
						if (ya < 0)
							ya = 0;
					}
				} else if (ya < 0) {
					double yld = to.y1 - from.y0;
					if (yld <= 0 && ya < yld) {
						closest = to;
						ya = yld + epsilon;
						if (ya > 0)
							ya = 0;
					}
				}
			}
		}
		if (closest != null && closest.owner != null) {
			closest.owner.handleCollision(this, oxa, oya);
		}
		if (xa != 0 || ya != 0) {
			pos.x += xa;
			pos.y += ya;
			return true;
		}
		return false;
	}


	@Override
	public void slideMove(double xa, double ya) {
		move(xa, ya);
	}
	
	public void derailify() {
		System.out.println("Derailify "+this.getClass().getName());
		this.remove();
		level.removeEntity(this);
		level.removeFromEntityMap(this);
		level.addEntity(new Turret(pos.x, pos.y, team, upgradeLevel));
	}
	
	public void drawRadius(Screen screen) {
		screen.opacityBlit(areaBitmap, (int) pos.x-radius, (int) pos.y-radius - yOffs, 0xDD);	
	}

}
