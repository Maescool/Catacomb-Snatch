package com.mojang.mojam.network.packet;

import java.io.*;

import com.mojang.mojam.network.Packet;

public class StartGamePacket extends Packet {

    private long gameSeed;
    private String levelFile;

    public StartGamePacket() {
    }

    public StartGamePacket(long gameSeed, String levelFile) {
        this.gameSeed = gameSeed;
        this.levelFile = levelFile;
    }

    @Override
    public void read(DataInputStream dis) throws IOException {
        gameSeed = dis.readLong();
        levelFile = dis.readUTF();
    }

    @Override
    public void write(DataOutputStream dos) throws IOException {
        dos.writeLong(gameSeed);
        dos.writeUTF(levelFile);
    }

    public long getGameSeed() {
        return gameSeed;
    }
    
    public String getLevelFile() {
    	return levelFile;
    }
}
