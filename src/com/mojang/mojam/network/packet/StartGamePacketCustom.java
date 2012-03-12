package com.mojang.mojam.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.mojang.mojam.level.Level;
import com.mojang.mojam.level.TileID;

public class StartGamePacketCustom extends StartGamePacket {

	private long gameSeed;
	private Level level;
	private int levelWidth, levelHeight;
	private Short[] shorts;
	private int difficulty;
	private int opponentCharacterID;

	public StartGamePacketCustom() {}

	public StartGamePacketCustom(long gameSeed, Level level, int difficulty, int opponentCharacterID) {
		this.gameSeed = gameSeed;
		this.level = level;
		this.difficulty = difficulty;
		this.opponentCharacterID = opponentCharacterID;
	}

	@Override
	public void read(DataInputStream dis) throws IOException {
		gameSeed = dis.readLong();
		levelWidth = dis.readInt();
		levelHeight = dis.readInt();
		opponentCharacterID = dis.readInt();

		shorts = new Short[levelWidth * levelHeight];
		for (int i = 0; i < shorts.length; i++) {
			shorts[i] = dis.readShort();
		}
		this.difficulty = dis.readInt();
	}

	@Override
	public void write(DataOutputStream dos) throws IOException {
		dos.writeLong(gameSeed);
		dos.writeInt(level.width);
		dos.writeInt(level.height);
		dos.writeInt(opponentCharacterID);
		for (int i = 0; i < level.activeTiles.length; i++) {
			dos.writeShort(TileID.tileToShort(level.activeTiles[i]));
		}
		dos.writeInt(difficulty);
	}

	@Override
	public long getGameSeed() {
		return gameSeed;
	}

	public Level getLevel() {
		level = new Level(levelWidth, levelHeight);
		for (int x = 0; x < level.width; x++) {
			for (int y = 0; y < level.width; y++) {
				int index = x + y * level.width;
				level.setTile(x, y, TileID.shortToTile(shorts[index], level, x, y));
			}
		}
		return level;
	}

	@Override
	public int getDifficulty() {
		return difficulty;
	}

	@Override
	public int getOpponentCharacterID() {
		return opponentCharacterID;
	}

}
