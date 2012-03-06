
package com.mojang.mojam.entity.weapon;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.Options;
import com.mojang.mojam.entity.Bullet;
import com.mojang.mojam.entity.BulletBuckshot;
import com.mojang.mojam.entity.Entity;
import com.mojang.mojam.entity.Player;
import com.mojang.mojam.entity.mob.Mob;
import com.mojang.mojam.network.TurnSynchronizer;
import com.mojang.mojam.screen.Art;

public class Shotgun extends Rifle {
	private int knockBack = 7;
	private int numberBullets = 7;
	
	private boolean readyToShoot = true;
	private int currentShootDelay = 0;	
	
	public Shotgun(Mob owner) {
		super(owner);
		image = Art.weaponList[1][0];
		setWeaponMode();
	}
	
	@Override
	public void setWeaponMode(){
		if(Options.getAsBoolean(Options.CREATIVE)){
			bulletDamage = 100f;
			accuracy = 0.06;
		}else{
			super.setWeaponMode();
		}
	}

	@Override
	public void primaryFire(double xDirection, double yDirection) {
		if (readyToShoot) {
			double direction;
			double directionSum = 0.0;
			
			for (int i = 0; i < numberBullets; i++) {
				if (owner.isSprint) {
					direction = getBulletDirection(accuracy * 2 * i);
				} else {
					direction = getBulletDirection(accuracy * i);
				}
				directionSum += direction;

				xDirection = Math.cos(direction);
				yDirection = Math.sin(direction);

				Entity bullet = getAmmo(xDirection, yDirection);

				owner.level.addEntity(bullet);

				if (owner instanceof Player) {
					Player player = (Player) owner;
					player.muzzleTicks = 3;
					player.muzzleX = bullet.pos.x + 7 * xDirection - 8;
					player.muzzleY = bullet.pos.y + 5 * yDirection - 8 + 1;
				}

			}
			
			double directionAverage = directionSum / numberBullets;
			xDirection = Math.cos(directionAverage);
			yDirection = Math.sin(directionAverage);
			applyImpuls(xDirection, yDirection, knockBack);
			
			currentShootDelay = shootDelay;
			MojamComponent.soundPlayer.playSound("/sound/Shot 1.wav",
					(float) owner.getPosition().x, (float) owner.getPosition().y);
			//Just fired so we are no longer ready to shoot
			readyToShoot = false;
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

	public Bullet getAmmo(double xDir, double yDir) {
		Bullet bullet = new BulletBuckshot(owner, xDir, yDir, bulletDamage);
		return bullet;
	}
}
