package com.mojang.mojam.network;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.mojang.mojam.network.kryo.Network.ChatMessage;
import com.mojang.mojam.network.kryo.Network.EndGameMessage;
import com.mojang.mojam.network.kryo.Network.TurnMessage;
import com.mojang.mojam.network.kryo.SnatchClient;

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

	private final SnatchClient snatchClient;
	private int localId;

	private boolean isStarted;
	private int stalled;

	public TurnSynchronizer(SnatchClient client, int localId, int numPlayers) {

		this.snatchClient = client;
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
							snatchClient.handleMessage(i, command);
						}
					}
				}
			}
			stalled = 0; //reset stall count
			return true;
		} else {
			//this happens if the server is not responding. If the server is gone, eventually the game will end.
			stalled++;
			if(stalled == 25) {
				snatchClient.handleMessage(0,new ChatMessage("Server Not Responding..."));
			}
			//give it a small amount of time to recover, but it probably won't
			if(stalled > 180) {
				//dead game
				snatchClient.sendMessage(new EndGameMessage());
				snatchClient.handleMessage(0,new EndGameMessage());
			}
		
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
		if (turnSequence%50 == 0) {
			snatchClient.ping();
		}
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

		if (snatchClient != null) {
			snatchClient.sendMessage((turnInfo.getLocalPacket(nextTurnMessages)));
		}

	}
	

	public void setStarted(boolean isStarted) {
		this.isStarted = isStarted;
	}

	public synchronized void onTurnMessage(TurnMessage message) {
		playerCommands.addPlayerCommands(message.playerId,
				message.turnNumber,message.list);
	}
	
	

	public synchronized void setSeed(long seed) {
		synchedSeed = seed;
		synchedRandom.setSeed(seed);
	}
	
	public synchronized void startGame(long seed) {
		setStarted(true);
		//setSeed(seed);
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
