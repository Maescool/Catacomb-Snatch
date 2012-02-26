package com.mojang.mojam.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.mojang.mojam.network.NetworkCommand;

public class ChangeMouseCoordinateCommand extends NetworkCommand {
	private int x;
	private int y;
	private boolean mouseHidden;

	public ChangeMouseCoordinateCommand() {
	}

	public ChangeMouseCoordinateCommand(int x, int y, boolean mouseHidden) {
		this.x = x;
		this.y = y;
		this.mouseHidden = mouseHidden;
	}

	@Override
	public void read(DataInputStream dis) throws IOException {
		x = dis.readInt();
		y = dis.readInt();
		mouseHidden = dis.readBoolean();
	}

	@Override
	public void write(DataOutputStream dos) throws IOException {
		dos.writeInt(x);
		dos.writeInt(y);
		dos.writeBoolean(mouseHidden);
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public boolean isMouseHidden() {
		return mouseHidden;
	}

}
