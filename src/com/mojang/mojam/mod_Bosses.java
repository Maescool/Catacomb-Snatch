package com.mojang.mojam;

import com.mojang.mojam.entity.mob.EntityBoss;

public class mod_Bosses extends Mod
{
	
	public mod_Bosses()
	{
		Snatch.addEntity(new EntityBoss(0,0,0));
	}

	@Override
	public String getVersion()
	{
		return "";
	}
	
	

}
