package com.mojang.mojam.network;

public interface MessageListener {

	public void handleMessage(int playerId, Object message);

}
