package com.mojang.mojam.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.mojang.mojam.network.NetworkCommand;

public class ChatCommand extends NetworkCommand {

	private String message;

	public ChatCommand() {}

	public ChatCommand(String message) {
		this.message = message;
	}

	@Override
	public void read(DataInputStream dis) throws IOException {
		int length = dis.readInt();
		char[] data = new char[length];
		for (int i = 0; i < length; i++) {
			data[i] = dis.readChar();
		}
		message = new String(data);
	}

	@Override
	public void write(DataOutputStream dos) throws IOException {
		dos.writeInt(message.length());
		dos.writeChars(message);
	}

	public String getMessage() {
		return message;
	}

}
