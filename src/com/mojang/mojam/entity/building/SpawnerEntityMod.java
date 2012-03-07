package com.mojang.mojam.entity.building;

import com.mojang.mojam.Snatch;
import com.mojang.mojam.entity.mob.Bat;
import com.mojang.mojam.entity.mob.Mob;
import com.mojang.mojam.entity.mob.Mummy;
import com.mojang.mojam.entity.mob.Scarab;
import com.mojang.mojam.entity.mob.Snake;

public class SpawnerEntityMod extends SpawnerEntity
{
	int type;

	public SpawnerEntityMod(double x, double y, int i)
	{
		super(x, y);
		type = i;
	}

	@Override
	public int getColor()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMiniMapColor()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getName()
	{
		// TODO Auto-generated method stub
		return Snatch.getEntityById(type, 0, 0).getClass().getSimpleName() + " Spawner";
	}

	@Override
	protected Mob getMob(double x, double y)
	{
		if((Mob) Snatch.getEntityById(type,x,y)!=null)return (Mob) Snatch.getEntityById(type,x,y);
		return (Mob) Snatch.getEntityById(type, x, y);
	}

}
