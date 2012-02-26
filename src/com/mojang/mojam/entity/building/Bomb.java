package com.mojang.mojam.entity.building;

import java.util.Set;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.entity.Bullet;
import com.mojang.mojam.entity.Entity;
import com.mojang.mojam.entity.animation.LargeBombExplodeAnimation;
import com.mojang.mojam.entity.mob.*;
import com.mojang.mojam.screen.*;

public class Bomb extends Building {

	public static final double BOMB_DISTANCE = 50;
	private boolean hit = false;
	private Bullet fakeBullet = new Bullet(MojamComponent.instance.player, 1,
			1, 0);

	public Bomb(double x, double y, int localTeam) {
		super(x, y, Team.Neutral, localTeam);
		setStartHealth(8);
		yOffs = 2;
		setSize(7, 7);
		doShowHealthBar = false;
	}

	public void die() {
		level.addEntity(new LargeBombExplodeAnimation(pos.x, pos.y));
		MojamComponent.soundPlayer.playSound("/sound/Explosion 2.wav",
				(float) pos.x, (float) pos.y);

		Set<Entity> entities = level.getEntities(pos.x - BOMB_DISTANCE, pos.y
				- BOMB_DISTANCE, pos.x + BOMB_DISTANCE, pos.y + BOMB_DISTANCE,
				Mob.class);
		for (Entity e : entities) {
			double distSqr = pos.distSqr(e.pos);
			if (distSqr < (BOMB_DISTANCE * BOMB_DISTANCE)) {
				((Mob) e).hurt(this, 5);
			}
		}
	}

	@Override
	public boolean isNotFriendOf(Mob m) {
		return true;
	}

	public void tick() {
		if (hit)
			super.hurt(fakeBullet, 2);

		if (health <= 0) {
			if (--hurtTime <= 0) {
				die();
				remove();
			}
			return;
		}

		super.tick();
		if (--freezeTime > 0)
			return;

	}

	public Bitmap getSprite() {
		return Art.bomb;
	}

	@Override
	public void hurt(Entity source, float damage) {

	}

	public void hit() {
		hit = true;
		;
	}
}
