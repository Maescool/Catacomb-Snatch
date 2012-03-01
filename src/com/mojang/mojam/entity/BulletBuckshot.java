package com.mojang.mojam.entity;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.entity.building.Bomb;
import com.mojang.mojam.entity.mob.*;
import com.mojang.mojam.screen.*;

public class BulletBuckshot extends Bullet {
	public double xa, ya;
	public Mob owner;
	boolean hit = false;
	public int life;
	private int facing;
	private float damage;

	public BulletBuckshot(Mob owner, double xa, double ya, float damage) {
		super(owner, ya, ya, damage);
		this.owner = owner;
		pos.set(owner.pos.x + xa * 4, owner.pos.y + ya * 4);
		this.xa = xa * 18;
		this.ya = ya * 18;
		this.setSize(2, 2);
		physicsSlide = false;
		life = 10;
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
		if(damage > 0.5)
		damage -= 0.5;
	}

	@Override
	protected boolean shouldBlock(Entity e) {
		if (e instanceof BulletBuckshot)
			return false;
		if ((e instanceof Mob) && !(e instanceof RailDroid) && !((Mob) e).isNotFriendOf(owner))
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
				((Bomb)entity).hit();
			} else if (mobEnt.isNotFriendOf(owner) || (entity instanceof RailDroid)) {
				mobEnt.hurt(this,damage);
				hit = true;
			}
		} else {
			entity.hurt(this);
			hit = true;
		}
		if (hit) {
			MojamComponent.soundPlayer.playSound("/sound/Shot 2.wav", (float) pos.x, (float) pos.y);
		}
	}
}
