package com.mojang.mojam.network.kryo;

import java.util.List;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;
import com.mojang.mojam.level.Level;
import com.mojang.mojam.network.NetworkCommand;

public class Network {

	static public final int port = 54555;

	// This registers objects that are going to be sent over the network.
	static public void register (EndPoint endPoint) {
		Kryo kryo = endPoint.getKryo();
		kryo.register(StartGameMessage.class);
		kryo.register(StartGameCustomMessage.class);
		kryo.register(TurnMessage.class);
	}

	static public class StartGameMessage {
		public long gameSeed;
		public String levelFile;
		public int difficulty;
	}
	
	static public class StartGameCustomMessage {
		public long gameSeed;
		public Level level;
		public int levelWidth, levelHeight;
		public Short[] shorts;
		public int difficulty;
		public int player1Character, player2Character;
	}
	
	static public class TurnMessage {
		public int playerId;
		public int turnNumber;
		public List<NetworkCommand> list;
	}
}
