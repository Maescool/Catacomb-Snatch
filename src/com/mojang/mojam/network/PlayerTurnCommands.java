package com.mojang.mojam.network;

import java.util.*;

public class PlayerTurnCommands {

	private List<List<PlayerCommands>> playerCommands;

	public PlayerTurnCommands(int numPlayers) {
		playerCommands = new ArrayList<List<PlayerCommands>>();
		for (int i = 0; i < numPlayers; i++) {
			playerCommands.add(i, new ArrayList<PlayerCommands>());
		}
	}

	public boolean isAllDone(int turnNumber) {
		for (List<PlayerCommands> commandList : playerCommands) {
			boolean found = false;
			for (PlayerCommands commands : commandList) {
				if (commands.turnNumber == turnNumber) {
					found = true;
					break;
				}
			}
			if (!found) {
				return false;
			}
		}
		return true;
	}

	public void addPlayerCommands(int playerId, int turnNumber,
			List<Object> commands) {
		playerCommands.get(playerId).add(
				new PlayerCommands(turnNumber, commands));
	}

	public List<Object> popPlayerCommands(int playerId, int turnNumber) {
		for (PlayerCommands commands : playerCommands.get(playerId)) {
			if (commands.turnNumber == turnNumber) {
				playerCommands.get(playerId).remove(commands);
				return commands.messages;
			}
		}
		return null;
	}

	private class PlayerCommands {
		private int turnNumber;
		private List<Object> messages;

		public PlayerCommands(int turnNumber, List<Object> messages) {
			this.turnNumber = turnNumber;
			this.messages = messages;
		}
	}

}
