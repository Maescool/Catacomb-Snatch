package com.mojang.mojam.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.mojang.mojam.network.NetworkCommand;

public class ChangeKeyCommand extends NetworkCommand {

	private boolean nextState;
	private int key;

	public ChangeKeyCommand() {
	}

	public ChangeKeyCommand(int key, boolean nextState) {
		this.key = key;
		this.nextState = nextState;
	}

	@Override
	public void read(DataInputStream dis) throws IOException {
		key = dis.readInt();
		nextState = dis.readBoolean();
	}

	@Override
	public void write(DataOutputStream dos) throws IOException {
		dos.writeInt(key);
		dos.writeBoolean(nextState);
	}

	public int getKey() {
		return key;
	}

	public boolean getNextState() {
		return nextState;
	}
}
