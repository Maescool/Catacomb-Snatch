package com.mojang.mojam.mod;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.level.AStar;
import com.mojang.mojam.level.Path;
import com.mojang.mojam.math.Vec2;
import com.mojang.mojam.network.Packet;

public class mod_testla extends Mod
{
	public mod_testla()
	{
		System.out.println("Testla Running!");
	}
	
	@Override
	public void OnSendPacket(Packet p)
	{
		System.out.println(p.getId());
	}
	
	@Override
	public void RunOnce()
	{
		System.out.println("\"Rawr\" - Charles Testla, 1898");		
	}
	@Override
	public void OnRender()
	{
	    if(MojamComponent.instance.player!= null)
	    {
		System.out.println(MojamComponent.instance.player.pos);
		AStar a = new AStar(MojamComponent.instance.player.level,MojamComponent.instance.player);
	    	Path path = a.getPath(MojamComponent.instance.player.pos, new Vec2(520, 520));
	    	path.render();
	    }
	}

	@Override
	public String getVersion()
	{
		return "Test-la!";
	}
}
