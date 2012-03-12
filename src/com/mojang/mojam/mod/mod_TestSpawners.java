package com.mojang.mojam.mod;

import com.mojang.mojam.Keys;
import com.mojang.mojam.Keys.Key;
import com.mojang.mojam.entity.Entity;
import com.mojang.mojam.entity.mob.Mob;
import com.mojang.mojam.entity.mob.TestEntity;

public class mod_TestSpawners extends Mod
{

	int id;
	int turrentId;
	long invulnTimer = 0;
	long frame = 0;
	long lastFrame = 0;
	int fps = 0;
	Key placeBomb = ModSystem.getMojam().keys.new Key("bomb");

	public mod_TestSpawners()
	{
		id = ModSystem.addEntity(new TestEntity(0, 0));
	}

	@Override
	public Entity getEntityInstanceById(int i, double x, double y)
	{
		Mob te = null;
		if(i == id) te = new TestEntity(x, y);
		return te;
	}
	
	@Override
	public void OnKeyPressed(Key k)
	{
		System.out.println("Pressed: "+k.name);
		if(k.equals(placeBomb))System.out.println("Bomb Pressed!");
	}
	
	@Override
	public void IfKeyDown(Key k)
	{
		System.out.println("Down: "+k.name);
		if(k.equals(placeBomb))System.out.println("Bomb Down!");
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
		/*if((frame % 500) < (lastFrame % 500))lastFrame = frame;
	    frame = Snatch.currentTimeMillis();
	    System.out.println(1000/(lastFrame));
	    fps = (int) (1000/(frame-lastFrame));
	    Snatch.getFont().draw(Snatch.getMojam().screen, Snatch.getMojam().texts.FPS(fps), 10, 10);*/
	}
	
	@Override
	public void RunOnce()
	{
		ModSystem.addKey(placeBomb, ModSystem.keycode("b"));
		//Snatch.addKey(placeBomb, );
	}

	@Override
	public String getVersion()
	{
		return "Test";
	}

}
