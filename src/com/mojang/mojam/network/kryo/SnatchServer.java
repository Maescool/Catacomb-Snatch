package com.mojang.mojam.network.kryo;

import java.io.IOException;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;
import com.mojang.mojam.network.kryo.Network.ChatMessage;
import com.mojang.mojam.network.kryo.Network.RegisterName;
import com.mojang.mojam.network.kryo.Network.StartGameCustomMessage;
import com.mojang.mojam.network.kryo.Network.StartGameMessage;
import com.mojang.mojam.network.kryo.Network.TurnMessage;

public class SnatchServer {
	Server server;

	public SnatchServer() throws IOException {
		Log.set(Log.LEVEL_DEBUG);
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
					if (name == null) return;
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
				
				if(object instanceof StartGameMessage) {
					StartGameMessage startGameMessage = (StartGameMessage) object;
					server.sendToAllExceptTCP(connection.getID(),startGameMessage);
					return;
				}
				
				if(object instanceof StartGameCustomMessage) {
					StartGameCustomMessage startGameMessage = (StartGameCustomMessage) object;
	
					server.sendToAllExceptTCP(connection.getID(),startGameMessage);
				}
				/*
				if(object instanceof PauseMessage) {
					PauseMessage pauseMessage = (PauseMessage) object;
	
					server.sendToAllExceptTCP(connection.getID(),pauseMessage);
				}
				
				if(object instanceof CharacterMessage) {
					CharacterMessage characterMessage = (CharacterMessage) object;
	
					server.sendToAllExceptTCP(connection.getID(),characterMessage);
				}
				
				if(object instanceof ChangeMouseButtonMessage) {
					ChangeMouseButtonMessage changeMouseButtonMessage = (ChangeMouseButtonMessage) object;
	
					server.sendToAllExceptTCP(connection.getID(),changeMouseButtonMessage);
				}
				
				if(object instanceof ChangeMouseCoordinateMessage) {
					ChangeMouseCoordinateMessage changeMouseCoordinateMessage = (ChangeMouseCoordinateMessage) object;
	
					server.sendToAllExceptTCP(connection.getID(),changeMouseCoordinateMessage);
				}
				
				if(object instanceof ChangeKeyMesasge) {
					ChangeKeyMesasge changeKeyMessage = (ChangeKeyMesasge) object;
	
					server.sendToAllExceptTCP(connection.getID(),changeKeyMessage);
				}
				
				if(object instanceof ChatMessage) {
					ChatMessage chatMessage = (ChatMessage) object;
	
					server.sendToAllExceptTCP(connection.getID(),chatMessage);
				}
				
				if(object instanceof PingMessage) {
					PingMessage pingMessage = (PingMessage) object;
	
					server.sendToAllExceptTCP(connection.getID(),pingMessage);
				}
		     */
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

	public static void main(String[] args) throws IOException {
		Log.set(Log.LEVEL_DEBUG);
		new SnatchServer();
	}

}
