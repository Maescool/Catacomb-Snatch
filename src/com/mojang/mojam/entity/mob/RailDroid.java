package com.mojang.mojam.entity.mob;


import com.mojang.mojam.entity.Bullet;
import com.mojang.mojam.Options;
import com.mojang.mojam.entity.Entity;
import com.mojang.mojam.entity.building.TreasurePile;
import com.mojang.mojam.entity.building.Turret;
import com.mojang.mojam.level.tile.*;
import com.mojang.mojam.math.Vec2;
import com.mojang.mojam.network.TurnSynchronizer;
import com.mojang.mojam.screen.*;

public class RailDroid extends Mob {
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

	private Direction dir = Direction.UNKNOWN;
	private Direction lDir = Direction.DOWN;
	private int noTurnTime = 0;
	private int pauseTime = 0;
	public boolean carrying = false;
	public int swapTime = 0;
	public int team;
	public static boolean creative = Options.getAsBoolean(Options.CREATIVE);

	public RailDroid(double x, double y, int team) {
		super(x, y, team);
		this.team = team;
		this.setSize(10, 8);
		deathPoints = 1;
		
		if(creative)
			isImmortal = true;
	}

	public void tick() {

		xBump = yBump = 0;

		super.tick();
		if (freezeTime > 0)
			return;
		if (swapTime > 0)
			swapTime--;
		boolean hadPaused = pauseTime > 0;
		if (pauseTime > 0) {
			pauseTime--;
			if (pauseTime > 0)
				return;
		}
		int xt = (int) (pos.x / Tile.WIDTH);
		int yt = (int) (pos.y / Tile.HEIGHT);

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
		double speed = 0.7;
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

		if (!carrying && swapTime == 0) {
			if (level.getEntities(getBB().grow(32), TreasurePile.class).size() > 0) {
				swapTime = 30;
				carrying = true;
			}
		}
		if (carrying && swapTime == 0) {
			if (pos.y < 8 * Tile.HEIGHT) {
				carrying = false;
				level.player2Score += 2;
			}
			if (pos.y > (level.height - 7 - 1) * Tile.HEIGHT) {
				carrying = false;
				level.player1Score += 2;
			}
		}
		// level.getTile(xt, yt)
	}

	@Override
	public Bitmap getSprite() {
		if (lDir == Direction.LEFT)
			return Art.raildroid[1][1];
		if (lDir == Direction.UP)
			return Art.raildroid[0][1];
		if (lDir == Direction.RIGHT)
			return Art.raildroid[1][0];
		if (lDir == Direction.DOWN)
			return Art.raildroid[0][0];
		return Art.raildroid[0][0];
	}

	public void handleCollision(Entity entity, double xa, double ya) {
		super.handleCollision(entity, xa, ya);
		if (entity instanceof RailDroid) {
			RailDroid other = (RailDroid) entity;
			if (other.carrying != carrying && carrying) {
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

				if (other.swapTime == 0 && swapTime == 0) {
					other.swapTime = swapTime = 15;

					boolean tmp = other.carrying;
					other.carrying = carrying;
					carrying = tmp;
				}
			}
		}
	}

	@Override
	protected boolean shouldBlock(Entity e) {
		// if (e instanceof Player && ((Player) e).team == team) return false;
		if(e instanceof Bullet && ((Bullet) e).owner instanceof Turret && ((Bullet) e).owner.team == team) return false;
		return super.shouldBlock(e);
	}

	public void render(Screen screen) {
		super.render(screen);
		if (carrying) {
			screen.blit(Art.bullets[0][0], pos.x - 8, pos.y - 20 - yOffs);
		} else {
			screen.blit(Art.bullets[1][1], pos.x - 8, pos.y - 20 - yOffs);
		}
	}

}
