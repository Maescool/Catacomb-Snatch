package com.mojang.mojam.entity.weapon;

import java.util.Set;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.Options;
import com.mojang.mojam.entity.Bullet;
import com.mojang.mojam.entity.Entity;
import com.mojang.mojam.entity.Player;
import com.mojang.mojam.entity.animation.LargeBombExplodeAnimation;
import com.mojang.mojam.entity.mob.Mob;
import com.mojang.mojam.network.TurnSynchronizer;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Bitmap;
import com.mojang.mojam.screen.Screen;

public class Cannon implements IWeapon
{

	protected Mob owner;
	protected static float BULLET_DAMAGE;

	private int upgradeIndex = 1;
	private double accuracy;
	private int shootDelay = 900;

	private boolean readyToShoot = true;
	private int currentShootDelay = 0;

	public Cannon(Mob mob)
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
			BULLET_DAMAGE = 30f;
			accuracy = 0f;
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
			
			xDir = Math.cos(dir);
			yDir = Math.sin(dir);
			applyImpuls(xDir, yDir, 10);

			bullet = getAmmo(xDir, yDir);

			owner.level.addEntity(bullet);

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
		Bullet bullet = new Bullet(owner, xDir, yDir, BULLET_DAMAGE)
		{
			@Override
			public void tick()
			{
				if (--duration <= -20) {
					remove();
					return;
				}
				if(!move(xa,ya))
				{
					if(move(-xa,ya))xa = -xa;
					if(move(xa,-ya))ya = -ya;
				}
				xa *= 0.95;
				ya *= 0.95;
			}
			
			@Override
			public void remove()
			{
				level.addEntity(new LargeBombExplodeAnimation(pos.x, pos.y));
				MojamComponent.soundPlayer.playSound("/sound/Explosion 2.wav",
						(float) pos.x, (float) pos.y);
				float BOMB_DISTANCE = 100;
				Set<Entity> entities = level.getEntities(pos.x - BOMB_DISTANCE, pos.y
						- BOMB_DISTANCE, pos.x + BOMB_DISTANCE, pos.y + BOMB_DISTANCE,
						Mob.class);
				for (Entity e : entities) {
					double distSqr = pos.distSqr(e.pos);
					if (distSqr < (BOMB_DISTANCE * BOMB_DISTANCE)) {
						((Mob) e).hurt(this, (float) (BULLET_DAMAGE*BULLET_DAMAGE/distSqr));
					}
				}
				super.remove();
			}
			
			@Override
			public void render(Screen screen)
			{
				screen.blit(Art.bomb, pos.x-16, pos.y-16);
			}
		};
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

	@Override
	public Bitmap getSprite()
	{
		// TODO Auto-generated method stub
		return Art.weaponList[0][0];
	}

	
}
