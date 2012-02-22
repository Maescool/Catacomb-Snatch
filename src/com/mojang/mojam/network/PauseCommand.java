package com.mojang.mojam.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PauseCommand extends NetworkCommand {

	private boolean newStatus;

	public PauseCommand() {

	}

	public PauseCommand(boolean nextState) {
		this.newStatus = nextState;
	}

	@Override
	public void read(DataInputStream dis) throws IOException {
		newStatus = dis.readBoolean();

	}

	@Override
	public void write(DataOutputStream dos) throws IOException {
		dos.writeBoolean(newStatus);

	}

	public boolean isPause() {
		return newStatus;
	}

}
