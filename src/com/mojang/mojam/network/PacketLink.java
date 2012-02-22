package com.mojang.mojam.network;

public interface PacketLink {

	public void sendPacket(Packet packet);

	public boolean tick();

	public void setPacketListener(PacketListener packetListener);

}
