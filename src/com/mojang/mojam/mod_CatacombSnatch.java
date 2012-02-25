package com.mojang.mojam;

import com.mojang.mojam.entity.Entity;
import com.mojang.mojam.entity.Player;
import com.mojang.mojam.entity.mob.Bat;
import com.mojang.mojam.entity.mob.Mob;
import com.mojang.mojam.entity.mob.Mummy;
import com.mojang.mojam.entity.mob.Scarab;
import com.mojang.mojam.entity.mob.Snake;

public class mod_CatacombSnatch extends Mod
{
	int[] ids = new int[4];
	
	public mod_CatacombSnatch()
	{
		ids[0]=Snatch.addEntity(new Bat(0, 0, 0));
		ids[1]=Snatch.addEntity(new Snake(0, 0, 0));
		ids[2]=Snatch.addEntity(new Mummy(0, 0, 0));
		ids[3]=Snatch.addEntity(new Scarab(0, 0, 0));
	}

	@Override
	public Entity getEntityInstanceById(int type, double x, double y)
	{
		Mob te = null;
		if(type == ids[0]) te = new Bat(x, y, 0);
		if(type == ids[1]) te = new Snake(x, y, 0);
		if(type == ids[2]) te = new Mummy(x, y, 0);
		if(type == ids[3]) te = new Scarab(x, y, 0);
		return te;
	}

	@Override
	public String getVersion()
	{
		return "";
	}

}
