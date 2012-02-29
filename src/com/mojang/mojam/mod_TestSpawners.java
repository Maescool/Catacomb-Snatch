package com.mojang.mojam;

import com.mojang.mojam.entity.Entity;
import com.mojang.mojam.entity.Player;
import com.mojang.mojam.entity.mob.Bat;
import com.mojang.mojam.entity.mob.Mob;
import com.mojang.mojam.entity.mob.Mummy;
import com.mojang.mojam.entity.mob.Snake;
import com.mojang.mojam.entity.mob.TestEntity;

public class mod_TestSpawners extends Mod
{

	int id;
	int turrentId;
	long invulnTimer = 0;
	long frame = 0;
	long lastframe = 0;
	double fps = 0;

	public mod_TestSpawners()
	{
		id = Snatch.addEntity(new TestEntity(0, 0, 0));
		System.out.println("TestEntity Id: " + id);
	}

	@Override
	public Entity getEntityInstanceById(int i, double x, double y)
	{
		Mob te = null;
		if(i == id) te = new TestEntity(x, y, 0);
		return te;
	}

	//Demonstration, TODO
	@Override
	public void OnTick()
	{
		/*Player player = Snatch.getMojam().player;
		if(invulnTimer > 0)
		{
			player.isImmortal = true;
			invulnTimer--;
		}
		else if(player.useMoney(1))
		{
			invulnTimer = 100;
		}
		else
		{
			player.isImmortal = false;
		}*/
		//System.out.println(fps);
	}
	
	@Override
	public void OnRender()
	{
		lastframe = frame;
		frame = System.nanoTime();
		fps = 1000000000/(frame - lastframe);
	}

	@Override
	public String getVersion()
	{
		return "Test";
	}

}
