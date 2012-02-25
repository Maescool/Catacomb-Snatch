package com.mojang.mojam.network;

import java.io.IOException;
import java.net.Socket;

public class ClientSidePacketLink extends NetworkPacketLink {

	public ClientSidePacketLink(String host, int port) throws IOException {
		super(new Socket(host, port));
	}

}
