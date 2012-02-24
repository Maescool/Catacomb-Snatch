package com.mojang.mojam.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.mojang.mojam.level.Level;
import com.mojang.mojam.level.LevelInformation;
import com.mojang.mojam.level.TileID;
import com.mojang.mojam.network.Packet;

public class StartPregamePacket extends Packet {
	
	private long gameSeed;
	public Level level;
	public int difficulty;
	public int levelWidth, levelHeight;
	public Short[] shorts;
	public LevelInformation levelInfo;
	
	public boolean doneRead;
	
	public StartPregamePacket(){
	}
	
	public StartPregamePacket(long gameSeed, Level level, int difficulty){
		this.gameSeed = gameSeed;
		this.level = level;
		this.difficulty = difficulty;
	}
	
	@Override
	public void read(DataInputStream dis) throws IOException {
		gameSeed = dis.readLong();
		levelInfo = LevelInformation.readMP(dis);
		levelWidth = dis.readInt();
		levelHeight = dis.readInt();
		
		shorts = new Short[levelWidth * levelHeight];
		for(int i = 0; i < shorts.length; i++){
			shorts[i] = dis.readShort();
		}
		this.difficulty = dis.readInt();
		doneRead = true;
	}

	@Override
	public void write(DataOutputStream dos) throws IOException {
		dos.writeLong(gameSeed);
		level.getInfo().sendMP(dos);
		dos.writeInt(level.width);
		dos.writeInt(level.height);
		for(int i = 0; i < level.tiles.length; i++){
			dos.writeShort(TileID.tileToShort(level.tiles[i]));
		}
		dos.writeInt(difficulty);
	}
	
	public Level getLevel() {
		Level level = new Level(levelWidth, levelHeight).setInfo(levelInfo);
		for(int x = 0; x < level.width; x++){
			for(int y = 0; y < level.width; y++){
				int index = x + y * level.width;
				level.setTile(x, y, TileID.shortToTile(shorts[index], level, x, y));
			}
		}
		return level;
	}

	public int getDifficulty() {
		return difficulty;
	}

	public long getGameSeed() {
		return gameSeed;
	}
}
