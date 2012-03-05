package com.mojang.mojam;

import com.mojang.mojam.entity.Entity;
import com.mojang.mojam.entity.mob.EntityBoss;

public class mod_Bosses extends Mod
{
	int type;
	public mod_Bosses()
	{
		type=Snatch.addEntity(new EntityBoss(0,0));
	}

	@Override
	public Entity getEntityInstanceById(int i, double x, double y)
	{
		if(i == type) return new EntityBoss(x,y);
		else return null;
	}
	
	@Override
	public String getVersion()
	{
		return "";
	}
	
	

}
