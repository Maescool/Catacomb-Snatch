package com.mojang.mojam.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.mojang.mojam.level.Level;
import com.mojang.mojam.level.TileID;

public class StartGamePacketCustom extends StartGamePacket {

	private long gameSeed;
	public Level level;
	public int levelWidth, levelHeight;
	public int playerCharacterID, opponentCharacterID;
	public Short[] shorts;
	public int difficulty;

	public StartGamePacketCustom() {}

	public StartGamePacketCustom(long gameSeed, Level level, int difficulty) {
		this.gameSeed = gameSeed;
		this.level = level;
		this.difficulty = difficulty;
	}

	@Override
	public void read(DataInputStream dis) throws IOException {
		gameSeed = dis.readLong();
		levelWidth = dis.readInt();
		levelHeight = dis.readInt();
		playerCharacterID = dis.readInt();
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
		dos.writeInt(level.getPlayerCharacterID());
		dos.writeInt(level.getOpponentCharacterID());
		for (int i = 0; i < level.tiles.length; i++) {
			dos.writeShort(TileID.tileToShort(level.tiles[i]));
		}
		dos.writeInt(difficulty);
	}

	public long getGameSeed() {
		return gameSeed;
	}

	public Level getLevel() {
		level = new Level(levelWidth, levelHeight, playerCharacterID, opponentCharacterID);
		for (int x = 0; x < level.width; x++) {
			for (int y = 0; y < level.width; y++) {
				int index = x + y * level.width;
				level.setTile(x, y, TileID.shortToTile(shorts[index], level, x, y));
			}
		}
		return level;
	}

	public int getDifficulty() {
		return difficulty;
	}

}
