package com.mojang.mojam;

import java.util.Date;

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
	}

	@Override
	public String getVersion()
	{
		return "Test-la!";
	}
}
