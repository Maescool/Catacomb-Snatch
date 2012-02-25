package com.mojang.mojam.entity.weapon;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.entity.Bullet;
import com.mojang.mojam.entity.Entity;
import com.mojang.mojam.entity.Player;
import com.mojang.mojam.network.TurnSynchronizer;

public class Rifle implements IWeapon {

	private Player owner;
	private static float BULLET_DAMAGE;
	
	private int upgradeIndex = 1;
	private double accuracy;
	private int shootDelay = 5;
	private int burstCount = 3;
	private int burstCooldown = 25;
	
	private boolean wasShooting;
	private int curShootDelay = 0;	
	
	private boolean isBursting;
	private int curBurstCount;
	private int curBurstDelay;	
	
	public Rifle(Player owner) {
		setOwner(owner);
		
		setWeaponMode();
		
		
	}
	public void setWeaponMode(){
		if(owner.creative == true){
			BULLET_DAMAGE = 100f;
			accuracy = 0;
		}else{
			BULLET_DAMAGE = .5f;
			accuracy = 0.15;
		}
	}
	
	@Override
	public void upgradeWeapon() {
		upgradeIndex++;
	}

	@Override
	public void primaryFire(double xDir, double yDir) {
		wasShooting = true;
		if (curShootDelay-- <= 0) {
			double dir = getBulletDirection(accuracy);
			xDir = Math.cos(dir);
			yDir = Math.sin(dir);			
			applyImpuls(xDir, yDir, 1);
			
			Entity bullet = new Bullet(owner, xDir, yDir, BULLET_DAMAGE);
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
		
		if(!isBursting) {
			curBurstDelay--;
		}
		isBursting = false;
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
