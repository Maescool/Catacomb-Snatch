package com.mojang.mojam.entity.building;

import com.mojang.mojam.entity.mob.Mob;
import com.mojang.mojam.mod.ModSystem;

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
		return ModSystem.getEntityById(type, 0, 0).getClass().getSimpleName() + " Spawner";
	}

	@Override
	protected Mob getMob(double x, double y)
	{
		if((Mob) ModSystem.getEntityById(type,x,y)!=null)return (Mob) ModSystem.getEntityById(type,x,y);
		return (Mob) ModSystem.getEntityById(type, x, y);
	}

}
