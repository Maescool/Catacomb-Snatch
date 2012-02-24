package com.mojang.mojam.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.mojang.mojam.network.Packet;

public class ChatPacket extends Packet {

	public int playerId;
	public String message;
	
	public ChatPacket(){
	}
	
	public ChatPacket(int playerId, String message){
		this.playerId = playerId;
		this.message = message;
	}
	
	
	@Override
	public void read(DataInputStream dis) throws IOException {
		playerId = dis.readInt();
		message = dis.readUTF();
	}

	@Override
	public void write(DataOutputStream dos) throws IOException {
		dos.writeInt(playerId);
		dos.writeUTF(message);
	}

}
