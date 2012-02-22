package com.mojang.mojam.network.packet;

import java.io.*;
import java.util.*;

import com.mojang.mojam.network.*;

public class TurnPacket extends Packet {

	private int playerId;
	private int turnNumber;
	private List<NetworkCommand> list;

	public TurnPacket() {

	}

	public TurnPacket(int localId, int turnNumber,
			List<NetworkCommand> localCommandList) {
		this.playerId = localId;
		this.turnNumber = turnNumber;
		if (localCommandList != null) {
			this.list = new ArrayList<NetworkCommand>(localCommandList);
		}
	}

	public int getPlayerId() {
		return playerId;
	}

	public int getTurnNumber() {
		return turnNumber;
	}

	public List<NetworkCommand> getPlayerCommandList() {
		return list;
	}

	@Override
	public void read(DataInputStream dis) throws IOException {
		playerId = dis.readInt();
		turnNumber = dis.readInt();
		int count = dis.readInt();
		if (count > 0) {
			list = new ArrayList<NetworkCommand>();
			for (int i = 0; i < count; i++) {
				NetworkCommand command = (NetworkCommand) Packet.readPacket(dis);
				if (command == null) {
					throw new IOException("Error reading command from player "
							+ playerId + " at turn " + turnNumber);
				}
				list.add(command);
			}
		}
	}

	@Override
	public void write(DataOutputStream dos) throws IOException {
		dos.writeInt(playerId);
		dos.writeInt(turnNumber);
		if (list != null) {
			dos.writeInt(list.size());
			for (NetworkCommand command : list) {
				Packet.writePacket(command, dos);
			}
		} else {
			dos.writeInt(0);
		}
	}

	@Override
	public void handle(PacketListener packetListener) {
		packetListener.handle(this);
	}
}
