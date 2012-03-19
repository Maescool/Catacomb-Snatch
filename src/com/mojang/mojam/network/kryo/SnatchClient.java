package com.mojang.mojam.network.kryo;

import java.awt.EventQueue;
import java.awt.Point;
import java.io.IOException;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage.Ping;
import com.esotericsoftware.kryonet.Listener;
import com.mojang.mojam.GameCharacter;
import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.gui.PauseMenu;
import com.mojang.mojam.gui.TitleMenu;
import com.mojang.mojam.level.DifficultyInformation;
import com.mojang.mojam.network.kryo.Network.ChangeKeyMessage;
import com.mojang.mojam.network.kryo.Network.ChangeMouseButtonMessage;
import com.mojang.mojam.network.kryo.Network.ChangeMouseCoordinateMessage;
import com.mojang.mojam.network.kryo.Network.CharacterMessage;
import com.mojang.mojam.network.kryo.Network.ChatMessage;
import com.mojang.mojam.network.kryo.Network.EndGameMessage;
import com.mojang.mojam.network.kryo.Network.PauseMessage;
import com.mojang.mojam.network.kryo.Network.RegisterName;
import com.mojang.mojam.network.kryo.Network.StartGameCustomMessage;
import com.mojang.mojam.network.kryo.Network.StartGameMessage;
import com.mojang.mojam.network.kryo.Network.TurnMessage;

public class SnatchClient {

	public int latency;
	
	Client client;
	private MojamComponent mojamComponent;

	public SnatchClient() {
		client = new Client();
		client.start();

		// For consistency, the classes to be sent over the network are
		// registered by the same method for both the client and server.
		Network.register(client);

		client.addListener(new Listener() {
			public void connected (Connection connection) {
				RegisterName registerName = new RegisterName();
				registerName.name = "USER"+Math.random();
				client.sendTCP(registerName);
				client.updateReturnTripTime();
			}

			public void received (Connection connection, Object object) {
			
				if (object instanceof Ping) {
					Ping ping = (Ping) object;
					if (ping.isReply)
						mojamComponent.latency = latency;	
				}  
				
				handleMessage(mojamComponent.localId, object);
				
			}

			public void disconnected (Connection connection) {
				EventQueue.invokeLater(new Runnable() {
					public void run () {
						//do disconnect stuff here...
					}
				});
			}
		});

	}

	public void connectLocal() {
		connect("localhost", Network.port);
	}
	
	public void connect(String host, int port) {
		try {
			client.connect(5000,host, port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void tick() {
		// nothing to do
	}

	public void sendMessage(Object message) {
		if (client.isConnected()) {
			client.sendTCP(message);
		}
	}

	public void setComponent(MojamComponent mojamComponent) {
		this.mojamComponent = mojamComponent;
	}


	public void handleMessage(int playerId, Object message) {

		if (message instanceof TurnMessage) {
			mojamComponent.synchronizer.onTurnMessage((TurnMessage) message);
		} else if (message instanceof RegisterName) {
			RegisterName registerNamemessage = (RegisterName) message;
			mojamComponent.createServerState = 1;
			return;
		} else if (message instanceof ChangeKeyMessage) {
			ChangeKeyMessage ckc = (ChangeKeyMessage) message;
			mojamComponent.synchedKeys[playerId].getAll().get(ckc.key).nextState = ckc.nextState;
		} else if (message instanceof ChangeMouseButtonMessage) {
			ChangeMouseButtonMessage ckc = (ChangeMouseButtonMessage) message;
			mojamComponent.synchedMouseButtons[playerId].nextState[ckc.button] = ckc.nextState;
		} else if (message instanceof ChangeMouseCoordinateMessage) {
			ChangeMouseCoordinateMessage ccc = (ChangeMouseCoordinateMessage) message;
			mojamComponent.synchedMouseButtons[playerId].setPosition(new Point(ccc.x, ccc.y));
			mojamComponent.synchedMouseButtons[playerId].mouseHidden = ccc.mouseHidden;
		} else if (message instanceof ChatMessage) {
			ChatMessage cc = (ChatMessage) message;
			mojamComponent.chat.addMessage(cc.message);
		} else if (message instanceof CharacterMessage) {
			CharacterMessage charMessage = (CharacterMessage) message;
			System.out.println(charMessage.localId);
			mojamComponent.players[charMessage.localId].setCharacter(GameCharacter.values()[charMessage.ordinal]);
		} else if (message instanceof PauseMessage) {
			PauseMessage pm = (PauseMessage) message;
			mojamComponent.paused = pm.paused;
			if (pm.paused) {
				mojamComponent.menuStack.add(new PauseMenu(MojamComponent.GAME_WIDTH, MojamComponent.GAME_HEIGHT));
			} else {
				mojamComponent.menuStack.pop();
			}
		} else if (message instanceof StartGameMessage) {
			if (!mojamComponent.isServer) {
				mojamComponent.sendCharacter = true;
				StartGameMessage sgMessage = (StartGameMessage) message;
				mojamComponent.synchronizer.setSeed(sgMessage.gameSeed);
				mojamComponent.createLevel(sgMessage.levelFile,
						TitleMenu.defaultGameMode,
						GameCharacter.values()[sgMessage.opponentCharacterID]);
				TitleMenu.difficulty = DifficultyInformation
						.getByInt(sgMessage.difficulty);
				mojamComponent.synchronizer.startGame(sgMessage.gameSeed);
			}
		} else if (message instanceof StartGameCustomMessage) {
			if (!mojamComponent.isServer) {
				mojamComponent.sendCharacter = true;
				StartGameCustomMessage sgMessage = (StartGameCustomMessage) message;
				mojamComponent.synchronizer.startGame(sgMessage.gameSeed);
				TitleMenu.difficulty = DifficultyInformation
						.getByInt(sgMessage.difficulty);
				mojamComponent.createLevel(sgMessage.levelFile,
						TitleMenu.defaultGameMode,
						GameCharacter.values()[sgMessage.opponentCharacterID]);
			}
		} else if (message instanceof EndGameMessage) {
			
			mojamComponent.handleAction(TitleMenu.RETURN_TO_TITLESCREEN);

		}

	}

	public void ping() {
		this.client.updateReturnTripTime();		
	}

	public void shutdown() {
		client.close();
	}

}
