package com.mojang.mojam.entity.mob;

import com.mojang.mojam.Snatch;
import com.mojang.mojam.entity.Entity;
import com.mojang.mojam.entity.IUsable;
import com.mojang.mojam.entity.building.Bomb;
import com.mojang.mojam.entity.building.Turret;
import com.mojang.mojam.math.Vec2;
import com.mojang.mojam.screen.Bitmap;

public class EntityBoss extends Mob
{

	public EntityBoss(double x, double y, int i)
	{
		super(x, y, Team.Neutral, i);
		localTeam = i;
		setPos(x, y);
		radius = new Vec2(20, 20);
		maxHealth = 50;
		deathPoints = 250;
	}

	@Override
	public Bitmap getSprite()
	{
		return null;
	}

	public double getSpeed()
	{
		return carrying != null ? 2 * CARRYSPEEDMOD : 2;
	}

	public void tick()
	{
		if(hurtTime > 0)
		{
			hurtTime--;
		}
		if(Snatch.getMojam().synchronizer.synchedRandom.nextInt(2000) == 0)
		{
			if(carrying == null) carrying = new Bomb(pos.x, pos.y, localTeam);
			else handleCarrying();
		}
		if(bounceWallTime > 0)
		{
			bounceWallTime--;
		}

		if(freezeTime > 0)
		{
			slideMove(xSlide, ySlide);
			xSlide *= 0.8;
			ySlide *= 0.8;

			if(xBump != 0 || yBump != 0)
			{
				move(xBump, yBump);
			}
			freezeTime--;
			return;
		}
		else
		{
			xSlide = ySlide = 0;
			if(health <= 0)
			{
				die();
				remove();
				return;
			}
		}
	}

	private void handleCarrying()
	{

		carrying.setPos(pos.x, pos.y - 20);
		if(!(carrying instanceof Turret))
		{
			carrying.tick();
		}

		if(Snatch.getMojam().synchronizer.synchedRandom.nextInt(2000) == 0 && (!(carrying instanceof IUsable) || (carrying instanceof IUsable && ((IUsable) carrying).isAllowedToCancel())))
		{
			dropCarrying();

		}
	}

	private void dropCarrying()
	{

		carrying.removed = false;
		carrying.xSlide = xto;
		carrying.ySlide = yto;
		carrying.freezeTime = 10;
		carrying.justDroppedTicks = 80;
		carrying.setPos(pos);
		level.addEntity(carrying);
		carrying = null;
	}
}
