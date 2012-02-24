package com.mojang.mojam.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.mojang.mojam.network.Packet;

public class StartGamePacketCustom extends Packet {

	public long seed;

	public StartGamePacketCustom() {}
	public StartGamePacketCustom(long seed) {
		this.seed = seed;
	}

	@Override
	public void read(DataInputStream dis) throws IOException {
		seed = dis.readLong();
	}

	@Override
	public void write(DataOutputStream dos) throws IOException {
		dos.writeLong(seed);
	}

}
