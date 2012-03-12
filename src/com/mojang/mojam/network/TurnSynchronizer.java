package com.mojang.mojam.network;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.mojang.mojam.network.packet.PingPacket;
import com.mojang.mojam.network.packet.StartGamePacket;
import com.mojang.mojam.network.packet.TurnPacket;

public class TurnSynchronizer {

	public static Random synchedRandom = new Random();
	public static long synchedSeed;

	public static final int TURN_QUEUE_LENGTH = 3;
	public static final int TICKS_PER_TURN = 5;

	private int currentTurnLength = TICKS_PER_TURN;

	private List<NetworkCommand> nextTurnCommands = new ArrayList<NetworkCommand>();
	private PlayerTurnCommands playerCommands;
	private final int numPlayers;

	private TurnInfo[] turnInfo = new TurnInfo[TURN_QUEUE_LENGTH];
	private int commandSequence = TURN_QUEUE_LENGTH - 1;
	private int turnSequence = 0;
	private int currentTurnTickCount;

	private final PacketLink packetLink;
	private int localId;
	private final CommandListener commandListener;

	private boolean isStarted;

	public TurnSynchronizer(CommandListener commandListener,
			PacketLink packetLink, int localId, int numPlayers) {

		this.commandListener = commandListener;
		this.packetLink = packetLink;
		this.localId = localId;
		this.numPlayers = numPlayers;
		this.playerCommands = new PlayerTurnCommands(numPlayers);

		for (int i = 0; i < turnInfo.length; i++) {
			turnInfo[i] = new TurnInfo(i, numPlayers);
		}
		turnInfo[0].isDone = true;
		turnInfo[1].isDone = true;

		synchedSeed = synchedRandom.nextLong();
		synchedRandom.setSeed(synchedSeed);

	}

	public int getLocalTick() {
		return turnSequence;
	}

	public synchronized boolean preTurn() {

		if (!isStarted) {
			return false;
		}

		int currentTurn = turnSequence % turnInfo.length;
		if (turnInfo[currentTurn].isDone
				|| playerCommands.isAllDone(turnSequence)) {
			turnInfo[currentTurn].isDone = true;

			if (!turnInfo[currentTurn].isCommandsPopped) {
				turnInfo[currentTurn].isCommandsPopped = true;

				for (int i = 0; i < numPlayers; i++) {
					List<NetworkCommand> commands = playerCommands
							.popPlayerCommands(i, turnSequence);
					if (commands != null) {
						for (NetworkCommand command : commands) {
							commandListener.handle(i, command);
						}
					}
				}
			}
			return true;
		} else {
			// System.out.println("Stalled");
		}
		return false;
	}

	public synchronized void postTurn() {

		currentTurnTickCount++;
		if (currentTurnTickCount >= currentTurnLength) {

			int currentTurn = turnSequence % turnInfo.length;
			turnInfo[currentTurn].clearDone();
			turnInfo[currentTurn].turnNumber += TURN_QUEUE_LENGTH;

			turnSequence++;
			currentTurnTickCount = 0;

			playerCommands.addPlayerCommands(localId, commandSequence,
					nextTurnCommands);
			sendLocalTurn(turnInfo[commandSequence % turnInfo.length]);
			commandSequence++;
			nextTurnCommands = null;
		}
		if (turnSequence%50 == 0) sendPingPacket();
	}

	public synchronized void addCommand(NetworkCommand command) {

		if (nextTurnCommands == null) {
			nextTurnCommands = new ArrayList<NetworkCommand>();
		}
		nextTurnCommands.add(command);

	}

	private void sendLocalTurn(TurnInfo turnInfo) {

		if (packetLink != null) {
			packetLink.sendPacket(turnInfo.getLocalPacket(nextTurnCommands));
		}

	}
	
	private void sendPingPacket() {
	    if (packetLink != null) {
	        packetLink.sendPacket(new PingPacket());
	    }
	}

	public void setStarted(boolean isStarted) {
		this.isStarted = isStarted;
	}

	public synchronized void onTurnPacket(TurnPacket packet) {
		playerCommands.addPlayerCommands(packet.getPlayerId(),
				packet.getTurnNumber(), packet.getPlayerCommandList());
	}

	public synchronized void onStartGamePacket(StartGamePacket packet) {
		setStarted(true);
		synchedSeed = packet.getGameSeed();
		synchedRandom.setSeed(synchedSeed);
	}

	public synchronized void onPingPacket(PingPacket packet) {
	    if (packet.getType() == PingPacket.TYPE_SYN && packetLink != null) {
	        packetLink.sendPacket(PingPacket.ack(packet));
	    }
	}
	
	private class TurnInfo {

		public boolean isCommandsPopped;
		public boolean isDone;
		private int turnNumber;

		public TurnInfo(int turnNumber, int numPlayers) {
			this.turnNumber = turnNumber;
		}

		public void clearDone() {
			isDone = false;
			isCommandsPopped = false;
		}

		public TurnPacket getLocalPacket(
				List<NetworkCommand> localPlayerCommands) {
			return new TurnPacket(localId, turnNumber, localPlayerCommands);
		}

		// public void onReceivedCommands(int playerId, List<NetworkCommand>
		// list) {
		// playerDone[playerId] = true;
		// playerCommands.set(playerId, list);
		//
		// checkDone();
		// }
		//
		// private void checkDone() {
		// isDone = true;
		// for (int i = 0; i < playerDone.length; i++) {
		// if (!playerDone[i]) {
		// isDone = false;
		// break;
		// }
		// }
		// }
	}

}
