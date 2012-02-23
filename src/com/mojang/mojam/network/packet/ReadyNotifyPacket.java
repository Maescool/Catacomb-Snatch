package com.mojang.mojam.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.entity.Player;
import com.mojang.mojam.network.Packet;


public class ReadyNotifyPacket extends Packet {
	
	public boolean[] ready;
	
	public ReadyNotifyPacket(){
	}
	
	@Override
	public void read(DataInputStream dis) throws IOException {
		ready = new boolean[dis.readShort()];
		for (int i = 0; i < ready.length; i++) {
			ready[i] = dis.readBoolean();
		}
	}

	@Override
	public void write(DataOutputStream dos) throws IOException {
		Player[] players = MojamComponent.instance.players;
		dos.writeShort(players.length);
		for (int i = 0; i < players.length; i++) {
			dos.writeBoolean(players[i].isReady);
		}
	}

}
