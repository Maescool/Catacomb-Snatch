package com.mojang.mojam.network;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.mojang.mojam.network.kryo.Network.PingMessage;
import com.mojang.mojam.network.kryo.Network.TurnMessage;
import com.mojang.mojam.network.kryo.SnatchClient;
import com.mojang.mojam.network.packet.StartGamePacket;
import com.mojang.mojam.network.packet.TurnPacket;

public class TurnSynchronizer {

	public static Random synchedRandom = new Random();
	public static long synchedSeed;

	public static final int TURN_QUEUE_LENGTH = 3;
	public static final int TICKS_PER_TURN = 5;

	private int currentTurnLength = TICKS_PER_TURN;

	//private List<NetworkCommand> nextTurnCommands = new ArrayList<NetworkCommand>();
	
	private List<Object> nextTurnMessages = new ArrayList<Object>();
	
	
	private PlayerTurnCommands playerCommands;
	private final int numPlayers;

	private TurnInfo[] turnInfo = new TurnInfo[TURN_QUEUE_LENGTH];
	private int commandSequence = TURN_QUEUE_LENGTH - 1;
	private int turnSequence = 0;
	private int currentTurnTickCount;

	private final SnatchClient client;
	private int localId;

	private boolean isStarted;

	public TurnSynchronizer(SnatchClient client, int localId, int numPlayers) {

		this.client = client;
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
					List<Object> commands = playerCommands
							.popPlayerCommands(i, turnSequence);
					if (commands != null) {
						for (Object command : commands) {
							client.handleMessage(i, command);
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
					nextTurnMessages);
			sendLocalTurn(turnInfo[commandSequence % turnInfo.length]);
			commandSequence++;
			nextTurnMessages = null;
		}
		if (turnSequence%50 == 0) sendPingPacket();
	}

//	public synchronized void addCommand(NetworkCommand command) {
//
	//	if (nextTurnCommands == null) {
		//	nextTurnCommands = new ArrayList<NetworkCommand>();
		//}
		//nextTurnCommands.add(command);

	//}
	
	public void addMessage(Object message) {
		if (nextTurnMessages == null) {
			nextTurnMessages = new ArrayList<Object>();
		}
		nextTurnMessages.add(message);
	}

	private void sendLocalTurn(TurnInfo turnInfo) {

		if (client != null) {
			client.sendMessage((turnInfo.getLocalPacket(nextTurnMessages)));
		}

	}
	
	private void sendPingPacket() {
	    if (client != null) {
	    	client.sendMessage(new PingMessage());
	    }
	}

	public void setStarted(boolean isStarted) {
		this.isStarted = isStarted;
	}

	public synchronized void onTurnMessage(TurnMessage message) {
		playerCommands.addPlayerCommands(message.playerId,
				message.turnNumber,message.list);
	}
	
	public synchronized void startGame(long seed) {
		setStarted(true);
		synchedSeed = seed;
		synchedRandom.setSeed(seed);
	}
	

	public synchronized void onPingPacket(PingMessage packet) {
	    if (packet.getType() == PingMessage.TYPE_SYN && client != null) {
	        client.sendMessage(PingMessage.ack(packet));
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

		public TurnMessage getLocalPacket(
				List<Object> localPlayerMessages) {
			return new TurnMessage(localId, turnNumber, localPlayerMessages);
		}
		
	}

}
