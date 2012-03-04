package com.mojang.mojam.entity.weapon;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.Options;
import com.mojang.mojam.entity.Bullet;
import com.mojang.mojam.entity.Entity;
import com.mojang.mojam.entity.Player;
import com.mojang.mojam.network.TurnSynchronizer;

public abstract class Weapon implements IWeapon {

	private Player owner;
	protected static float BULLET_DAMAGE;
	
	protected int upgradeIndex = 1;
	protected double accuracy = 0.15;
	protected int shootDelay = 5;
	
	private boolean readyToShoot = true;
	private int currentShootDelay = 0;	
	protected String soundDir = "/sound/Shot 1.wav";
	
	public Weapon(Player owner, float bulletDamage, double accuracy) {
		setOwner(owner);
		setWeaponMode(bulletDamage, accuracy);
	}
	
	protected void setSoundDir(String soundDir) {
		this.soundDir = soundDir;
	}
	
	protected String getSoundDir() {
		return this.soundDir;
	}
	
	public void setWeaponMode(float bulletDamage, double accuracy){
		if (Options.getAsBoolean(Options.CREATIVE)) {
			BULLET_DAMAGE = bulletDamage * 10;
			this.accuracy = 0;
		} else {
			BULLET_DAMAGE = bulletDamage;
			this.accuracy = accuracy;
		}
	}
	
	@Override
	public void upgradeWeapon() {
		upgradeIndex++;
	}

	@Override
	public void primaryFire(double xDir, double yDir) {
		if (readyToShoot) {
			double dir;
			if(owner.isSprint)
				dir = getBulletDirection(accuracy * 2);
			else
				dir = getBulletDirection(accuracy);
			xDir = Math.cos(dir);
			yDir = Math.sin(dir);			
			applyImpuls(xDir, yDir, 1);
			
			Entity bullet = new Bullet(owner, xDir, yDir, BULLET_DAMAGE);
			owner.level.addEntity(bullet);
			
			owner.muzzleTicks = 3;
			owner.muzzleX = bullet.pos.x + 7 * xDir - 8;
			owner.muzzleY = bullet.pos.y + 5 * yDir - 8 + 1;
			currentShootDelay = shootDelay;
			readyToShoot= false;
			MojamComponent.soundPlayer.playSound(getSoundDir(),
					(float) owner.getPosition().x, (float) owner.getPosition().y);
		}		
	}

	@Override
	public void weapontick() {
		if(!readyToShoot) {
			if(currentShootDelay > 0) currentShootDelay--;
			else readyToShoot = true;
		}
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
