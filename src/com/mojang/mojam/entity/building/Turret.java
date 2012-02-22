package com.mojang.mojam.entity.building;

import java.util.Set;

import com.mojang.mojam.entity.*;
import com.mojang.mojam.entity.mob.*;
import com.mojang.mojam.level.tile.Tile;
import com.mojang.mojam.screen.*;

public class Turret extends Building {

	private int delayTicks = 0;
	private int delay;
	private double radius;
	private double radiusSqr;

	private int[] upgradeRadius = new int[] { 3 * Tile.WIDTH, 5 * Tile.WIDTH, 7 * Tile.WIDTH };
	private int[] upgradeDelay = new int[] { 24, 21, 18 };

	private int facing = 0;

	public Turret(double x, double y, int team) {
		super(x, y, team);
		setStartHealth(10);
		freezeTime = 10;

		makeUpgradeableWithCosts(new int[] { 500, 1000, 5000 });
	}

	public void init() {
	}

	public void tick() {
		super.tick();
		if (--freezeTime > 0)
			return;
		if (--delayTicks > 0)
			return;

		Set<Entity> entities = level.getEntities(pos.x - radius, pos.y - radius, pos.x + radius, pos.y + radius);

		Entity closest = null;
		double closestDist = 99999999.0f;
		for (Entity e : entities) {
			if (!(e instanceof Mob) || e instanceof RailDroid || e instanceof Bomb)
				continue;
			if (!((Mob) e).isNotFriendOf(this))
				continue;
			final double dist = e.pos.distSqr(pos);
			if (dist < radiusSqr && dist < closestDist && !isTargetBehindWall(e.pos.x, e.pos.y)) {
				closestDist = dist;
				closest = e;
			}
		}
		if (closest == null)
			return;

		double invDist = 1.0 / Math.sqrt(closestDist);
		double yd = closest.pos.y - pos.y;
		double xd = closest.pos.x - pos.x;
		double angle = (Math.atan2(yd, xd) + Math.PI * 1.625);
		facing = (8 + (int) (angle / Math.PI * 4)) & 7;
		Bullet bullet = new Bullet(this, xd * invDist, yd * invDist);
		bullet.pos.y -= 10;
		level.addEntity(bullet);

		if (upgradeLevel > 0) {
			Bullet second_bullet = new Bullet(this, xd * invDist, yd * invDist);
			level.addEntity(second_bullet);
			if (facing == 0 || facing == 4) {
				bullet.pos.x -= 5;
				second_bullet.pos.x += 5;
			}
		}

		delayTicks = delay;
	}

	private boolean isTargetBehindWall(double dx2, double dy2) {
		int x1 = (int) pos.x / Tile.WIDTH;
		int y1 = (int) pos.y / Tile.HEIGHT;
		int x2 = (int) dx2 / Tile.WIDTH;
		int y2 = (int) dy2 / Tile.HEIGHT;

		Bullet bullet = new Bullet(this, pos.x, pos.y);

		int dx, dy, inx, iny, e;
		Tile temp;

		dx = x2 - x1;
		dy = y2 - y1;
		inx = dx > 0 ? 1 : -1;
		iny = dy > 0 ? 1 : -1;

		dx = java.lang.Math.abs(dx);
		dy = java.lang.Math.abs(dy);

		if (dx >= dy) {
			dy <<= 1;
			e = dy - dx;
			dx <<= 1;
			while (x1 != x2) {
				temp = level.getTile(x1, y1);
				if (!temp.canPass(bullet))
					return true;
				if (e >= 0) {
					y1 += iny;
					e -= dx;
				}
				e += dy;
				x1 += inx;
			}
		} else {
			dx <<= 1;
			e = dx - dy;
			dy <<= 1;
			while (y1 != y2) {
				temp = level.getTile(x1, y1);
				if (!temp.canPass(bullet))
					return true;
				if (e >= 0) {
					x1 += inx;
					e -= dy;
				}
				e += dx;
				y1 += iny;
			}
		}
		temp = level.getTile(x1, y1);
		if (!temp.canPass(bullet))
			return true;
		return false;
	}

	public void render(Screen screen) {
		super.render(screen);

		if (health < maxHealth)
			addHealthBar(screen, health, maxHealth);
	}

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

	protected void upgradeComplete() {
		maxHealth += 10;
		health = maxHealth;
		delay = upgradeDelay[upgradeLevel];
		radius = upgradeRadius[upgradeLevel];
		radiusSqr = radius * radius;
	}
}
