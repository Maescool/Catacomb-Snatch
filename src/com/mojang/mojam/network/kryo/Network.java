package com.mojang.mojam.network.kryo;

import java.util.ArrayList;
import java.util.List;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;
import com.esotericsoftware.kryonet.FrameworkMessage.Ping;
import com.mojang.mojam.level.Level;

public class Network {

	static public final int port = 3000;

	// This registers objects that are going to be sent over the network.
	static public void register (EndPoint endPoint) {
		Kryo kryo = endPoint.getKryo();
		kryo.register(RegisterName.class);
		kryo.register(StartGameMessage.class);
		kryo.register(StartGameCustomMessage.class);
		kryo.register(TurnMessage.class);
		kryo.register(PauseMessage.class);
		kryo.register(CharacterMessage.class);
		kryo.register(ChangeMouseButtonMessage.class);
		kryo.register(ChangeMouseCoordinateMessage.class);
		kryo.register(ChangeKeyMessage.class);
		kryo.register(ChatMessage.class);
		kryo.register(ArrayList.class);
		kryo.register(Ping.class);
		kryo.register(EndGameMessage.class);
		kryo.register(ConsoleMessage.class);
	}
	
	static public class RegisterName {
		public String name;
		public String version;
	}

	static public class StartGameMessage {
		
		public long gameSeed;
		public String levelFile;
		public int difficulty;
		public int opponentCharacterID;
		public StartGameMessage(){}
		public StartGameMessage(long gameSeed, String levelFile, int difficulty, int opponentCharacterID) {
			this.gameSeed = gameSeed;
			this.levelFile = levelFile;
			this.difficulty = difficulty;
			this.opponentCharacterID = opponentCharacterID;
		}
		
	}
	
	static public class StartGameCustomMessage {
		public long gameSeed;
		public Level level;
		public String levelFile;
		public int levelWidth, levelHeight;
		public Short[] shorts;
		public int difficulty;
		public int player1Character, opponentCharacterID;
		public StartGameCustomMessage(){}
		public StartGameCustomMessage(long gameSeed, Level level, int difficulty, int opponentCharacterID) {
			this.gameSeed = gameSeed;
			this.level = level;
			this.difficulty = difficulty;
			this.opponentCharacterID = opponentCharacterID;
		}
		
	}
	
	static public class TurnMessage {
		public int playerId;
		public int turnNumber;
		public List<Object> list;
		public TurnMessage(){}
		public TurnMessage(int localId, int turnNumber,
				List<Object> localMessageList) {
			this.playerId = localId;
			this.turnNumber = turnNumber;
			if (localMessageList != null) {
				this.list = new ArrayList<Object>(localMessageList);
			}
		}
		
	}

	static public class PauseMessage {
		public boolean paused;
		
		public PauseMessage() {
			paused=true;
		}
		public PauseMessage(boolean paused) {
			this.paused=paused;
		}
		
	}
	
	static public class CharacterMessage {		
		public int localId;
		public int ordinal;
	
		public CharacterMessage(){}
		public CharacterMessage(int localId, int ordinal) {
			this.localId = localId;
			this.ordinal = ordinal;
		}
		
	}
	
	static public class ChangeMouseButtonMessage {
		public boolean nextState;
		public int button;
		
		public ChangeMouseButtonMessage(){}
		public ChangeMouseButtonMessage(int button, boolean nextState) {
			this.button = button;
			this.nextState = nextState;
		}
	}
	
	static public class ChangeMouseCoordinateMessage {		
		public int x;
		public int y;
		public boolean mouseHidden;
		
		public ChangeMouseCoordinateMessage(){}
		public ChangeMouseCoordinateMessage(int x, int y, boolean mouseHidden) {
			this.x = x;
			this.y = y;
			this.mouseHidden = mouseHidden;
		}
		
	}
	
	static public class ChangeKeyMessage {
		public boolean nextState;
		public int key;
		
		public ChangeKeyMessage(){}
		public ChangeKeyMessage(int key, boolean nextState) {
			this.key = key;
			this.nextState = nextState;
		}
		
	}
	
	static public class ChatMessage {
		public String message;
		
		public ChatMessage(){}
		public ChatMessage(String message) {
			this.message = message;
		}

	}
	
	static public class ConsoleMessage {
		public String message;
		
		public ConsoleMessage(){}
		public ConsoleMessage(String message) {
			this.message = message;
		}

	}
	

	static public class EndGameMessage {
		public int end;
	}
	
}
