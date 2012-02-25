package com.mojang.mojam.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.mojang.mojam.network.NetworkCommand;

public class PauseCommand extends NetworkCommand {

	private boolean newPauseState;

	public PauseCommand() {

	}

	public PauseCommand(boolean nextState) {
		this.newPauseState = nextState;
	}

	@Override
	public void read(DataInputStream dis) throws IOException {
		newPauseState = dis.readBoolean();

	}

	@Override
	public void write(DataOutputStream dos) throws IOException {
		dos.writeBoolean(newPauseState);

	}

	public boolean isPaused() {
		return newPauseState;
	}

}
