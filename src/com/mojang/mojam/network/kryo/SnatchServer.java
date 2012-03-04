package com.mojang.mojam.network.kryo;

import java.io.IOException;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;
import com.mojang.mojam.network.kryo.Network.StartGameCustomMessage;
import com.mojang.mojam.network.kryo.Network.StartGameMessage;
import com.mojang.mojam.network.kryo.Network.TurnMessage;

public class SnatchServer {
	Server server;

	public SnatchServer() throws IOException {
		server = new Server() {
			protected Connection newConnection() {
				// By providing our own connection implementation, we can store
				// per
				// connection state without a connection ID to state look up.
				return new SnatchConnection();
			}
		};

		// For consistency, the classes to be sent over the network are
		// registered by the same method for both the client and server.
		Network.register(server);

		server.addListener(new Listener() {
			public void received(Connection c, Object object) {
				// We know all connections for this server are actually
				// ChatConnections.
				SnatchConnection connection = (SnatchConnection) c;
				
				if(object instanceof TurnMessage) {
					TurnMessage turnMessage = (TurnMessage) object;
					server.sendToAllExceptTCP(connection.getID(),turnMessage);
					//synchronizer.onTurnPacket((TurnMessage) packet);
					return;
				}
				
				if(object instanceof StartGameMessage) {
					StartGameMessage startGameMessage = (StartGameMessage) object;
					//if (!isServer) {
					//	StartGamePacket sgPacker = (StartGamePacket) packet;
					//	synchronizer.onStartGamePacket(sgPacker);
					//	TitleMenu.difficulty = DifficultyList.getDifficulties().get(sgPacker.getDifficulty());
					//	createLevel(sgPacker.getLevelFile(), TitleMenu.defaultGameMode);
					//}
					
					server.sendToAllExceptTCP(connection.getID(),startGameMessage);
					return;
				}
				
				if(object instanceof StartGameCustomMessage) {
					StartGameCustomMessage startGameMessage = (StartGameCustomMessage) object;
				//	if (!isServer) {
				//		StartGamePacketCustom sgPacker = (StartGamePacketCustom) packet;
				//		synchronizer.onStartGamePacket((StartGamePacket)packet);
				//		TitleMenu.difficulty = DifficultyList.getDifficulties().get(sgPacker.getDifficulty());
				//		level = sgPacker.getLevel();
				//		paused = false;
				//		initLevel();
				//	}
					
					server.sendToAllExceptTCP(connection.getID(),startGameMessage);
				}
				
			//	if(object instanceof PingMessage) {
			//	    PingPacket pp = (PingPacket)packet;
			//	    synchronizer.onPingPacket(pp);
			//	    if (pp.getType() == PingPacket.TYPE_ACK) {
			//	        addToLatencyCache(pp.getLatency());
			//	    }
			//	    server.sendToAllExceptTCP(connection.getID(),turnMessage);
			//	}
				
			}

		//	public void disconnected(Connection c) {
		//		SnatchConnection connection = (SnatchConnection) c;
		//		if (connection.name != null) {
		//			// Announce to everyone that someone (with a registered
		//			// name) has left.
		//			ChatMessage chatMessage = new ChatMessage();
		//			chatMessage.text = connection.name + " disconnected.";
		//			server.sendToAllTCP(chatMessage);
		//			updateNames();
		//		}
		//	}
		});
		server.bind(Network.port);
		server.start();
		
	}


	// This holds per connection state.
	static class SnatchConnection extends Connection {
		public String name;
	}

	public static void main(String[] args) throws IOException {
		Log.set(Log.LEVEL_DEBUG);
		new SnatchServer();
	}

}
