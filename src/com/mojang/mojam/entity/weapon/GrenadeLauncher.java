package com.mojang.mojam.entity.weapon;

import com.mojang.mojam.MojamComponent;

import com.mojang.mojam.entity.Entity;
import com.mojang.mojam.entity.GrenadeBullet;
import com.mojang.mojam.entity.Player;
import com.mojang.mojam.network.TurnSynchronizer;

public class GrenadeLauncher implements IWeapon {

	private Player owner;
	
	private static final float BULLET_DAMAGE = 15f;
	
	private double accuracy = 0.25;
	private int shootDelay = 40;
	
	private boolean wasShooting;
	private int curShootDelay = 0;	
	
	public GrenadeLauncher(Player owner) {
		setOwner(owner);
	}
	
	@Override
	public void upgradeWeapon() {
	}

	@Override
	public void primaryFire(double xDir, double yDir) {
		wasShooting = true;
		if (curShootDelay-- <= 0) {
			double dir = getBulletDirection(accuracy);
			xDir = Math.cos(dir);
			yDir = Math.sin(dir);			
			applyImpuls(xDir, yDir, 3);
			
			Entity bullet = new GrenadeBullet(owner, xDir, yDir, BULLET_DAMAGE);
			owner.level.addEntity(bullet);
			
			owner.muzzleTicks = 3;
			owner.muzzleX = bullet.pos.x + 7 * xDir - 8;
			owner.muzzleY = bullet.pos.y + 5 * yDir - 8 + 1;
			curShootDelay = shootDelay;
			MojamComponent.soundPlayer.playSound("/sound/Shot 1.wav",
					(float) owner.getPosition().x, (float) owner.getPosition().y);
		}		
	}

	@Override
	public void weapontick() {
		if(!wasShooting) {
			curShootDelay = 0;
		}
		wasShooting = false;
	}
	
	private double getBulletDirection(double accuracy) {
		double dir = Math.atan2(owner.aimVector.y, owner.aimVector.x)
				+ (TurnSynchronizer.synchedRandom.nextFloat() - TurnSynchronizer.synchedRandom
						.nextFloat()) * accuracy;
		
		return dir;
	}
	
	private void applyImpuls(double xDir, double yDir, double strength) {		
		owner.xd -= xDir * strength;
		owner.yd -= yDir * strength;
	}

	@Override
	public void setOwner(Player player) {
		this.owner = player;
	}
}