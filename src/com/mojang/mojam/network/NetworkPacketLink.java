package com.mojang.mojam.network;

import java.io.*;
import java.net.Socket;
import java.util.*;

public class NetworkPacketLink implements PacketLink {

	private static final int SEND_BUFFER_SIZE = 1024 * 5;

	private Socket socket;

	private Object writeLock = new Object();

	private List<Packet> incoming = Collections
			.synchronizedList(new ArrayList<Packet>());
	private List<Packet> outgoing = Collections
			.synchronizedList(new ArrayList<Packet>());

	private DataInputStream inputStream;
	private DataOutputStream outputStream;

	private Thread writeThread;
	private Thread readThread;

	private boolean isRunning = true;
	private boolean isQuitting = false;
	private boolean isDisconnected = false;

	private PacketListener packetListener;

	public NetworkPacketLink(Socket socket) throws IOException {
		this.socket = socket;
		socket.setTcpNoDelay(true);
		inputStream = new DataInputStream(socket.getInputStream());
		outputStream = new DataOutputStream(new BufferedOutputStream(
				socket.getOutputStream(), SEND_BUFFER_SIZE));

		readThread = new Thread("Read thread") {
			public void run() {
				try {
					while (isRunning && !isQuitting) {
						while (readTick())
							;

						try {
							sleep(2L);
						} catch (InterruptedException e) {
						}
					}
				} catch (Exception e) {
				}
			}
		};

		writeThread = new Thread("Write thread") {
			public void run() {
				try {
					while (isRunning) {
						while (writeTick())
							;

						try {
							if (outputStream != null)
								outputStream.flush();
						} catch (IOException e) {
							e.printStackTrace();
							break;
						}

						try {
							sleep(2L);
						} catch (InterruptedException e) {
						}
					}
				} catch (Exception e) {
				}
			}
		};

		readThread.start();
		writeThread.start();
	}

	public void tick() {
		int max = 1000;
		while (!incoming.isEmpty() && max-- >= 0) {
			Packet packet = incoming.remove(0);
			if (packetListener != null) {
				packet.handle(packetListener);
			}
		}
	}

	public void sendPacket(Packet packet) {
		if (isQuitting) {
			return;
		}
		synchronized (writeLock) {
			outgoing.add(packet);
		}
	}

	private boolean readTick() {
		boolean didSomething = false;
		try {
			Packet packet = Packet.readPacket(inputStream);

			if (packet != null) {
				if (!isQuitting) {
					incoming.add(packet);
				}
				didSomething = true;
			}
		} catch (Exception e) {
			if (!isDisconnected)
				handleException(e);
			return false;
		}
		return didSomething;
	}

	private boolean writeTick() {
		boolean didSomething = false;
		try {
			if (!outgoing.isEmpty()) {
				Packet packet;
				synchronized (writeLock) {
					packet = outgoing.remove(0);
				}
				Packet.writePacket(packet, outputStream);
				didSomething = true;
			}
		} catch (Exception e) {
			if (!isDisconnected)
				handleException(e);
			return false;
		}
		return didSomething;
	}

	private void handleException(Exception e) {
		e.printStackTrace();
		isDisconnected = true;
		try {
			socket.close();
		} catch (IOException e1) {
		}
	}

	public void setPacketListener(PacketListener packetListener) {
		this.packetListener = packetListener;
	}

}
