package com.mojang.mojam.entity.weapon;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.Options;
import com.mojang.mojam.entity.BulletBuckshot;
import com.mojang.mojam.entity.Entity;
import com.mojang.mojam.entity.Player;
import com.mojang.mojam.network.TurnSynchronizer;

public class Shotgun implements IWeapon {

	private Player owner;
	private static float BULLET_DAMAGE;
	
	private int upgradeIndex = 1;
	private double accuracy;
	private int shootDelay = 35;
	private int knockBack = 7;
	private int numberBullets = 7;
	
	private boolean readyToShoot = true;
	private int currentShootDelay = 0;	
	
	public Shotgun(Player owner) {
		setOwner(owner);
		
		setWeaponMode();
		
		if(owner.isSprint)
			shootDelay *= 2;
		
	}
	
	public void setWeaponMode(){
		if(Options.getAsBoolean(Options.CREATIVE)){
			BULLET_DAMAGE = 100.0f;
			accuracy = 0.12;
		}else{
			BULLET_DAMAGE = 4.5f;
			accuracy = 0.12;
		}
	}
	
	@Override
	public void upgradeWeapon() {
		upgradeIndex++;
	}

	@Override
	public void primaryFire(double xDirection, double yDirection) {
		if (readyToShoot) {
			double direction;
			double directionSum = 0.0;
			
			for(int i = 0; i < numberBullets; i++) {
			if(owner.isSprint)
				direction = getBulletDirection(accuracy * 2 * i);
			else
				direction = getBulletDirection(accuracy * i);
			directionSum += direction;
			
			xDirection = Math.cos(direction);
			yDirection = Math.sin(direction);
			
			Entity bullet = new BulletBuckshot(owner, xDirection, yDirection, BULLET_DAMAGE);
			owner.level.addEntity(bullet);
			
			//Muzzle flash
			owner.muzzleTicks = 3;
			owner.muzzleX = bullet.pos.x + 7 * xDirection - 8;
			owner.muzzleY = bullet.pos.y + 5 * yDirection - 8 + 1;
			
			}
			
			double directionAverage = directionSum/numberBullets;
			xDirection = Math.cos(directionAverage);
			yDirection = Math.sin(directionAverage);
			applyImpuls(xDirection, yDirection, knockBack);
			
			currentShootDelay = shootDelay;
			//Just fired so we are no longer ready to shoot
			readyToShoot = false;
			MojamComponent.soundPlayer.playSound("/sound/Shot 1.wav",
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
