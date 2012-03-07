package com.mojang.mojam.entity.weapon;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.Options;
import com.mojang.mojam.entity.Bullet;
import com.mojang.mojam.entity.Entity;
import com.mojang.mojam.entity.Player;
import com.mojang.mojam.entity.mob.Mob;
import com.mojang.mojam.network.TurnSynchronizer;

public class Shotgun implements IWeapon
{

	protected Mob owner;
	protected static float BULLET_DAMAGE;

	private int upgradeIndex = 1;
	private double accuracy;
	private int shootDelay = 40;

	private boolean readyToShoot = true;
	private int currentShootDelay = 0;

	public Shotgun(Mob mob)
	{
		setOwner(mob);

		setWeaponMode();

		if(mob.isSprint) shootDelay *= 3;

	}

	public void setWeaponMode()
	{
		if(Options.getAsBoolean(Options.CREATIVE))
		{
			BULLET_DAMAGE = 100f;
			accuracy = 0;
		}
		else
		{
			BULLET_DAMAGE = 5f;
			accuracy = 0.1f;
		}
	}

	@Override
	public void upgradeWeapon()
	{
		upgradeIndex++;
	}

	@Override
	public void primaryFire(double xDir, double yDir)
	{
		if(readyToShoot)
		{
			double dir;
			if(owner.isSprint) dir = getBulletDirection(accuracy * 2);
			else dir = getBulletDirection(accuracy);
			Entity bullet = null;
			final float spread = 0.07F;
			final int pellets = 7;
			for(int i = 0; i < pellets; i++)
			{
				xDir = Math.cos(dir-(spread*((pellets/2)-1))+(i*spread));
				yDir = Math.sin(dir-(spread*((pellets/2)-1))+(i*spread));
				applyImpuls(xDir, yDir, 2);	
				
				bullet = getAmmo(xDir, yDir);

				owner.level.addEntity(bullet);
			}

			if(owner instanceof Player)
			{
				Player player = (Player) owner;
				player.muzzleTicks = 3;
				player.muzzleX = bullet.pos.x + 7 * xDir - 8;
				player.muzzleY = bullet.pos.y + 5 * yDir - 8 + 1;
			}

			currentShootDelay = shootDelay;
			readyToShoot = false;
			MojamComponent.soundPlayer.playSound("/sound/Shot 1.wav", (float) owner.getPosition().x, (float) owner.getPosition().y);
		}
	}

	public Bullet getAmmo(double xDir, double yDir)
	{
		Bullet bullet = new Bullet(owner, xDir, yDir, BULLET_DAMAGE);
		return bullet;
	}

	@Override
	public void weapontick()
	{
		if(!readyToShoot)
		{
			if(currentShootDelay > 0) currentShootDelay--;
			else readyToShoot = true;
		}
	}

	private double getBulletDirection(double accuracy)
	{
		double dir = Math.atan2(owner.aimVector.y, owner.aimVector.x) + (TurnSynchronizer.synchedRandom.nextFloat() - TurnSynchronizer.synchedRandom.nextFloat()) * accuracy;

		return dir;
	}

	private void applyImpuls(double xDir, double yDir, double strength)
	{
		owner.xd -= xDir * strength;
		owner.yd -= yDir * strength;
	}

	@Override
	public void setOwner(Mob mob)
	{
		this.owner = mob;
	}

}
