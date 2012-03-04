package com.mojang.mojam.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.mojang.mojam.network.NetworkCommand;

public class ChangeMouseButtonCommand extends NetworkCommand {
	
	private boolean nextState;
	private int button;

	public ChangeMouseButtonCommand() {
	}

	public ChangeMouseButtonCommand(int button, boolean nextState) {
		this.button = button;
		this.nextState = nextState;
	}

	@Override
	public void read(DataInputStream dis) throws IOException {
		button = dis.readInt();
		nextState = dis.readBoolean();
	}

	@Override
	public void write(DataOutputStream dos) throws IOException {
		dos.writeInt(button);
		dos.writeBoolean(nextState);
	}

	public int getButton() {
		return button;
	}

	public boolean getNextState() {
		return nextState;
	}
	

}
