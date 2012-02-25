package com.mojang.mojam;

import com.mojang.mojam.entity.Entity;
import com.mojang.mojam.entity.mob.Team;
import com.mojang.mojam.level.Level;
import com.mojang.mojam.network.Packet;

public abstract class Mod
{
	public void OnTick(){}
	
	public void AfterTick(){}
	
	public void OnStartRender(){}
	
	public void OnRender(){}
	
	public void RunOnce(){}
	
	public void OnClose(){}
	
	public void OnSendPacket(Packet packet){}
	
	public void OnQuit(){}
	
	public void OnVictory(int team){}
	
	public void OnLevelTick(Level level){}
	
	public void OnReceivePacket(Packet packet){}
	
	public void CreateLevel(Level level){}
	
	public Entity getEntityInstanceById(int i, double x, double y)
	{
		return null;
	}
	
	public abstract String getVersion(); 
}
