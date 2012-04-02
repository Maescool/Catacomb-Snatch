package com.mojang.mojam.network.kryo;

import java.io.IOException;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.network.kryo.Network.ChatMessage;
import com.mojang.mojam.network.kryo.Network.ConsoleMessage;
import com.mojang.mojam.network.kryo.Network.EndGameMessage;
import com.mojang.mojam.network.kryo.Network.RegisterName;
import com.mojang.mojam.network.kryo.Network.StartGameCustomMessage;
import com.mojang.mojam.network.kryo.Network.StartGameMessage;
import com.mojang.mojam.network.kryo.Network.TurnMessage;

public class SnatchServer {
	Server server;

	public SnatchServer() throws IOException {
		//Log.set(Log.LEVEL_DEBUG);
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
				// SnatchConnections.
				SnatchConnection connection = (SnatchConnection) c;
				
				if (object instanceof RegisterName) {
					// Ignore the object if a client has already registered a name. This is
					// impossible with our client, but a hacker could send messages at any time.
					if (connection.name != null) return;
					// Ignore the object if the name is invalid.
					String name = ((RegisterName)object).name;
					
					String version = ((RegisterName)object).version;
					if(!version.equals(MojamComponent.GAME_VERSION)) {
						//version mismatch - send message about it and end the game
						server.sendToTCP(connection.getID(), new ChatMessage(MojamComponent.texts.getStatic("mp.mismatch")));
						server.sendToTCP(connection.getID(), new ChatMessage(MojamComponent.texts.getStatic("mp.server") + ": " + MojamComponent.GAME_VERSION));
						server.sendToTCP(connection.getID(), new ChatMessage(MojamComponent.texts.getStatic("mp.client") + ": " + version));
						
						server.sendToTCP(connection.getID(), new EndGameMessage());
						connection.close();
						return;
					}
					
					if (name == null) {
						return;
					}
					
					name = name.trim();
					if (name.length() == 0) return;
					// Store the name on the connection.
					connection.name = name;
					
					server.sendToAllExceptTCP(connection.getID(), (RegisterName)object);
					
					// Send a "connected" message to everyone except the new client.
					ChatMessage chatMessage = new ChatMessage();
					chatMessage.message = name + " connected.";
					server.sendToAllExceptTCP(connection.getID(), chatMessage);
					// Send everyone a new list of connection names.
					//updateNames();
					return;
				}
				
				if(object instanceof TurnMessage) {
					TurnMessage turnMessage = (TurnMessage) object;
					server.sendToAllExceptTCP(connection.getID(),turnMessage);
					//synchronizer.onTurnPacket((TurnMessage) packet);
					return;
				}
				

				if(object instanceof ConsoleMessage) {
					ConsoleMessage consoleMessage = (ConsoleMessage) object;
					server.sendToAllExceptTCP(connection.getID(),consoleMessage);
					return;
				}
				
				if(object instanceof StartGameMessage) {
					StartGameMessage startGameMessage = (StartGameMessage) object;
					server.sendToAllExceptTCP(connection.getID(),startGameMessage);
					return;
				}
				
				if(object instanceof StartGameCustomMessage) {
					StartGameCustomMessage startGameMessage = (StartGameCustomMessage) object;
	
					server.sendToAllExceptTCP(connection.getID(),startGameMessage);
				}
				
				if(object instanceof EndGameMessage) {
					EndGameMessage endGameMessage = (EndGameMessage) object;
					server.sendToAllExceptTCP(connection.getID(),endGameMessage);
					return;
				}
				
				
			}
			
			public void disconnected (Connection c) {
				SnatchConnection connection = (SnatchConnection)c;
				if (connection.name != null) {
					// Announce to everyone that someone has left.
					ChatMessage chatMessage = new ChatMessage();
					chatMessage.message =  connection.name + " disconnected.";
					server.sendToAllExceptTCP(connection.getID(), chatMessage);
					
					if(server.getConnections().length < 2) { //less than two players? dead game
						System.out.println("Ending Game");
						EndGameMessage endGameMessage = new EndGameMessage();
						server.sendToAllExceptTCP(connection.getID(), endGameMessage);
					}
					
					//updateNames();
				}
			}
		});
		
		server.bind(Network.port);
		server.start();
	}


	public void stop() {
		server.stop();
	}
	
	// This holds per connection state.
	static class SnatchConnection extends Connection {
		public String name;
	}

	public void shutdown() {
		server.close();
		server.stop();
	}

}
