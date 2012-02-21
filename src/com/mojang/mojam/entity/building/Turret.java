package com.mojang.mojam.entity.building;

import java.util.Set;

import com.mojang.mojam.entity.*;
import com.mojang.mojam.entity.mob.*;
import com.mojang.mojam.gui.Font;
import com.mojang.mojam.level.tile.Tile;
import com.mojang.mojam.screen.*;

public class Turret extends Building {

	private int delayTicks = 0;
	private int delay;
	private double radius;
	private double radiusSqr;

	private int[] upgradeRadius = new int[] { 3 * Tile.WIDTH, 5 * Tile.WIDTH,
			7 * Tile.WIDTH };
	private int[] upgradeDelay = new int[] { 24, 21, 18 };

	private int facing = 0;

	public Turret(double x, double y, int team) {
		super(x, y, team);
		setStartHealth(20);
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

		Set<Entity> entities = level.getEntities(pos.x - radius,
				pos.y - radius, pos.x + radius, pos.y + radius);

		Entity closest = null;
		double closestDist = 99999999.0f;
		for (Entity e : entities) {
			if (!(e instanceof Mob))
				continue;
			if ((e instanceof RailDroid))
				continue;
			if (!((Mob) e).isNotFriendOf(this))
				continue;
			if ((e instanceof TreasurePile))
				continue;
			final double dist = e.pos.distSqr(pos);
			if (dist < radiusSqr && dist < closestDist) {
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
		
		if (upgradeLevel > 0){
		    Bullet second_bullet = new Bullet(this, xd * invDist, yd * invDist);
            level.addEntity(second_bullet);
            if (facing == 0 || facing == 4){
                bullet.pos.x -= 5;
                second_bullet.pos.x += 5;
            }
		}
		
		delayTicks = delay;
	}

	public void render(Screen screen) {
		super.render(screen);
		if (health != 0) {
			Font.drawCentered(screen, health + " / " + maxHealth,
					(int) (pos.x), (int) (pos.y + 12));
		}
	}

	public Bitmap getSprite() {
	    switch (upgradeLevel){
	    case 1:
	        return Art.turret2[facing][0];
	    case 2:
	        return Art.turret3[facing][0];
	    default:
	        return Art.turret[facing][0];
	    }
	}

	protected void upgradeComplete() {
		delay = upgradeDelay[upgradeLevel];
		radius = upgradeRadius[upgradeLevel];
		radiusSqr = radius * radius;
	}
}
