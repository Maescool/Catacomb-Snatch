package com.mojang.mojam.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.mojang.mojam.network.Packet;

public class StartGamePacket extends Packet {

	private long gameSeed;
	private String levelFile;
	private int difficulty;
	private int opponentCharacterID;

	public StartGamePacket() {}

	public StartGamePacket(long gameSeed, String levelFile, int difficulty, int opponentCharacterID) {
		this.gameSeed = gameSeed;
		this.levelFile = levelFile;
		this.difficulty = difficulty;
		this.opponentCharacterID = opponentCharacterID;
	}

	@Override
	public void read(DataInputStream dis) throws IOException {
		gameSeed = dis.readLong();
		levelFile = dis.readUTF();
		difficulty = dis.readInt();
		opponentCharacterID = dis.readInt();
	}

	@Override
	public void write(DataOutputStream dos) throws IOException {
		dos.writeLong(gameSeed);
		dos.writeUTF(levelFile);
		dos.writeInt(difficulty);
		dos.writeInt(opponentCharacterID);
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

	public int getOpponentCharacterID() {
		return opponentCharacterID;
	}
}
