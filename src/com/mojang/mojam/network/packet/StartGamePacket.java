package com.mojang.mojam.network.packet;

import java.io.*;

import com.mojang.mojam.network.Packet;

public class StartGamePacket extends Packet {

	private long gameSeed;
	private String levelFile;
	private int difficulty;

	public StartGamePacket() {
	}

	public StartGamePacket(long gameSeed, String levelFile, int difficulty) {
		this.gameSeed = gameSeed;
		this.levelFile = levelFile;
		this.difficulty = difficulty;
	}

	@Override
	public void read(DataInputStream dis) throws IOException {
		gameSeed = dis.readLong();
		levelFile = dis.readUTF();
		difficulty = dis.readInt();
	}

	@Override
	public void write(DataOutputStream dos) throws IOException {
		dos.writeLong(gameSeed);
		dos.writeUTF(levelFile);
		dos.writeInt(difficulty);
	}

	public long getGameSeed() {
		return gameSeed;
	}

	public String getLevelFile() {
		return levelFile;
	}

	public int getDifficulty() {
		return difficulty;
	}
}
