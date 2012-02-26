package com.mojang.mojam.entity;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.entity.building.Bomb;
import com.mojang.mojam.entity.mob.*;
import com.mojang.mojam.screen.*;

public class Bullet extends Entity {
	public double xa, ya;
	public Mob owner;
	boolean hit = false;
	public int life;
	private int facing;
	private float damage;

	public Bullet(Mob e, double xa, double ya, float damage) {
		this.owner = e;
		pos.set(e.pos.x + xa * 4, e.pos.y + ya * 4);
		this.xa = xa * 6;
		this.ya = ya * 6;
		this.setSize(4, 4);
		physicsSlide = false;
		life = 40;
		double angle = (Math.atan2(ya, xa) + Math.PI * 1.625);
		facing = (8 + (int) (angle / Math.PI * 4)) & 7;
		this.damage = damage;
	}

	@Override
	public void tick() {
		if (--life <= 0) {
			remove();
			return;
		}
		if (!move(xa, ya)) {
			hit = true;
		}
		if (hit && !removed) {
			remove();
		}
	}

	@Override
	protected boolean shouldBlock(Entity e) {
		if (e instanceof Bullet)
			return false;
		if ((e instanceof Mob) && !(e instanceof RailDroid)
				&& !((Mob) e).isNotFriendOf(owner))
			return false;
		return e != owner;
	}

	@Override
	public void render(Screen screen) {
		screen.blit(Art.bullet[facing][0], pos.x - 8, pos.y - 10);
	}

	@Override
	public void collide(Entity entity, double xa, double ya) {
		if (entity instanceof Mob) {
			Mob mobEnt = (Mob) entity;
			if (entity instanceof Bomb) {
				((Bomb) entity).hit();
			} else if (mobEnt.isNotFriendOf(owner)
					|| (entity instanceof RailDroid)) {
				mobEnt.hurt(this, damage);
				hit = true;
			}
		} else {
			entity.hurt(this);
			hit = true;
		}
		if (hit) {
			MojamComponent.soundPlayer.playSound("/sound/Shot 2.wav",
					(float) pos.x, (float) pos.y);
		}
	}
}
