package com.mojang.mojam.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.mojang.mojam.network.NetworkCommand;

public class CharacterCommand extends NetworkCommand {

	private int playerID;
	private int characterID;

	public CharacterCommand() {}

	public CharacterCommand(int playerID, int characterID) {
		this.playerID = playerID;
		this.characterID = characterID;
	}

	public int getPlayerID() {
		return playerID;
	}

	public int getCharacterID() {
		return characterID;
	}

	@Override
	public void read(DataInputStream dis) throws IOException {
		playerID = dis.readInt();
		characterID = dis.readInt();
	}

	@Override
	public void write(DataOutputStream dos) throws IOException {
		dos.writeInt(playerID);
		dos.writeInt(characterID);
	}

}
