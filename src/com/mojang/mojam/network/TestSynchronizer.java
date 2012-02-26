package com.mojang.mojam.network;

import java.net.*;

import com.mojang.mojam.network.packet.TurnPacket;

public class TestSynchronizer {

	private static ServerSocket serverSocket;

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		PacketLink packetLink = null;
		int localId = 0;
		if (args.length > 0 && args[0].equals("server")) {

			serverSocket = new ServerSocket(3000);
			Socket socket = serverSocket.accept();
			packetLink = new NetworkPacketLink(socket);

		} else {
			packetLink = new ClientSidePacketLink("localhost", 3000);
			localId = 1;
		}

		// TestPacketLink link1 = new TestPacketLink();
		// TestPacketLink link2 = new TestPacketLink();

		PlayThread thread1 = new PlayThread(localId, packetLink);
		// PlayThread thread2 = new PlayThread(1, link2);

		// link1.setTarget(thread2.turnSynchronizer);
		// link2.setTarget(thread1.turnSynchronizer);

		thread1.start();
		// thread2.start();

	}

	private static class PlayThread extends Thread implements PacketListener {

		private TurnSynchronizer turnSynchronizer;
		private final int localId;
		private final PacketLink packetLink;

		public PlayThread(int localId, PacketLink packetLink) {
			this.localId = localId;
			this.packetLink = packetLink;
			turnSynchronizer = new TurnSynchronizer(null, packetLink, localId,
					2);
			packetLink.setPacketListener(this);
		}

		@Override
		public void run() {

			while (true) {
				packetLink.tick();
				if (turnSynchronizer.preTurn()) {
					System.out.println(localId + ": "
							+ turnSynchronizer.getLocalTick());
					turnSynchronizer.postTurn();
				}
				try {
					sleep(16L);
				} catch (InterruptedException e) {
					e.printStackTrace();
					break;
				}
			}

		}

		public void handle(Packet packet) {
			if (packet instanceof TurnPacket) {
				turnSynchronizer.onTurnPacket((TurnPacket) packet);
			}
		}
	}

	// private static class TestPacketLink implements PacketLink {
	//
	// private TurnSynchronizer target;
	// private List<Packet> packetQueue = new ArrayList<Packet>();
	//
	// public TestPacketLink() {
	// }
	//
	// public void setTarget(TurnSynchronizer target) {
	// this.target = target;
	// }
	//
	// public synchronized void sendPacket(Packet packet) {
	// packetQueue.add(packet);
	// }
	//
	// public synchronized void tick() {
	// for (Packet packet : packetQueue) {
	// if (packet instanceof TurnPacket) {
	// target.onTurnPacket((TurnPacket) packet);
	// }
	// }
	// packetQueue.clear();
	// }
	//
	// }

}
