package com.mojang.mojam.network;


public abstract class NetworkCommand extends Packet {

    @Override
    public void handle(PacketListener packetListener) {
        throw new RuntimeException("Commands should be handled by the turn synchronizer");
    }

}
