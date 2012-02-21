package com.mojang.mojam.network;

public interface CommandListener {

	public void handle(int playerId, NetworkCommand packet);

}
