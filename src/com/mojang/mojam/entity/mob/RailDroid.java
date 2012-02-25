package com.mojang.mojam.entity.mob;

import com.mojang.mojam.entity.Bullet;
import com.mojang.mojam.entity.Entity;
import com.mojang.mojam.entity.building.TreasurePile;
import com.mojang.mojam.entity.building.Turret;
import com.mojang.mojam.level.tile.*;
import com.mojang.mojam.math.Vec2;
import com.mojang.mojam.network.TurnSynchronizer;
import com.mojang.mojam.screen.*;

public class RailDroid extends Mob {
	private int dir = 0;
	private int lDir = 4;
	private int noTurnTime = 0;
	private int pauseTime = 0;
	public boolean carrying = false;
	public int swapTime = 0;
	public int team;

	public RailDroid(double x, double y, int team, int localTeam) {
		super(x, y, team, localTeam);
		this.team = team;
		this.setSize(10, 8);
		deathPoints = 1;
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

		if (noTurnTime == 0 && (!cr || dir == 0 || centerIsh)) {
			noTurnTime = 4;
			// int nd = 0;
			if (dir == 1 && lr)
				lWeight += 16;
			if (dir == 2 && ur)
				uWeight += 16;
			if (dir == 3 && rr)
				rWeight += 16;
			if (dir == 4 && dr)
				dWeight += 16;

			if (lWeight + uWeight + rWeight + dWeight == 0) {
				if ((dir == 1 || dir == 3)) {
					if (ur)
						uWeight += 4;
					if (dr)
						dWeight += 4;
				}
				if ((dir == 2 || dir == 4)) {
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

			if (dir == 1)
				rWeight = 0;
			if (dir == 2)
				dWeight = 0;
			if (dir == 3)
				lWeight = 0;
			if (dir == 4)
				uWeight = 0;

			int totalWeight = lWeight + uWeight + rWeight + dWeight;
			if (totalWeight == 0) {
				dir = 0;
			} else {
				int res = TurnSynchronizer.synchedRandom.nextInt(totalWeight);
				// dir = 0;
				dir = (((dir - 1) + 2) & 3) + 1;

				uWeight += lWeight;
				rWeight += uWeight;
				dWeight += rWeight;

				if (res < lWeight)
					dir = 1;
				else if (res < uWeight)
					dir = 2;
				else if (res < rWeight)
					dir = 3;
				else if (res < dWeight)
					dir = 4;
			}

			// dir = nd;
		}

		if (cr) {
			double r = 1;
			if (!(dir == 1 || dir == 3) && xcd < -r)
				xd += 0.3;
			if (!(dir == 1 || dir == 3) && xcd > +r)
				xd -= 0.3;
			if (!(dir == 2 || dir == 4) && ycd < -r)
				yd += 0.3;
			if (!(dir == 2 || dir == 4) && ycd > +r)
				yd -= 0.3;
			// if (!(dir == 1 || dir == 3) && xcd >= -r && xcd <= r) xd = -xcd;
			// if (!(dir == 2 || dir == 4) && ycd >= -r && ycd <= r) yd = -ycd;
		}
		double speed = 0.7;
		if (dir > 0)
			lDir = dir;
		if (dir == 1)
			xd -= speed;
		if (dir == 2)
			yd -= speed;
		if (dir == 3)
			xd += speed;
		if (dir == 4)
			yd += speed;

		Vec2 oldPos = pos.clone();
		move(xd, yd);
		if (dir > 0 && oldPos.distSqr(pos) < 0.1 * 0.1) {
			if (hadPaused) {
				dir = (((dir - 1) + 2) & 3) + 1;
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
		if (lDir == 1)
			return Art.raildroid[1][1];
		if (lDir == 2)
			return Art.raildroid[0][1];
		if (lDir == 3)
			return Art.raildroid[1][0];
		if (lDir == 4)
			return Art.raildroid[0][0];
		return Art.raildroid[0][0];
	}

	public void handleCollision(Entity entity, double xa, double ya) {
		super.handleCollision(entity, xa, ya);
		if (entity instanceof RailDroid) {
			RailDroid other = (RailDroid) entity;
			if (other.carrying != carrying && carrying) {
				if (lDir == 1 && other.pos.x > pos.x - 4)
					return;
				if (lDir == 2 && other.pos.y > pos.y - 4)
					return;
				if (lDir == 3 && other.pos.x < pos.x + 4)
					return;
				if (lDir == 4 && other.pos.y < pos.y + 4)
					return;

				if (other.lDir == 1 && pos.x > other.pos.x - 4)
					return;
				if (other.lDir == 2 && pos.y > other.pos.y - 4)
					return;
				if (other.lDir == 3 && pos.x < other.pos.x + 4)
					return;
				if (other.lDir == 4 && pos.y < other.pos.y + 4)
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
