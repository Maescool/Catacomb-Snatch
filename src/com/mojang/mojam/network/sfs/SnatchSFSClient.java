package com.mojang.mojam.network.sfs;

import java.util.List;
import java.util.ListIterator;

import com.mojang.mojam.MojamComponent;
import com.smartfoxserver.v2.exceptions.SFSException;

import sfs2x.client.SmartFox;
import sfs2x.client.core.BaseEvent;
import sfs2x.client.core.IEventListener;
import sfs2x.client.core.SFSEvent;
import sfs2x.client.entities.Room;
import sfs2x.client.entities.User;
import sfs2x.client.requests.JoinRoomRequest;
import sfs2x.client.requests.LoginRequest;
import sfs2x.client.requests.SubscribeRoomGroupRequest;


public class SnatchSFSClient implements IEventListener {
	
	private MojamComponent mojamComponent;
	private SmartFox sfsClient;
	// The zone in which the user will be logged in.
	private static final String SFS_ZONE = "CatacombSnatch";
		
	/** Stores the login error message. It's used by the login error dialog box.*/
	private String mLoginError = "";
	
	/** The ListAdapter with the user names. */
	private List<User> mUsers;
	private List<String> mGameList;
	
	private int latency;
	
	//Strings used to set up room group and extension
	private String GAME_ROOMS_GROUP_NAME = "games";
	private String EXTENSION_ID = "CatacombSnatch";
	private String EXTENSIONS_CLASS = "net.catacombsnatch.sfs2x.CatacombSnatch";
	
	private String SERVER_HOSTNAME = "sfs.catacombsnatch.net";
	private int SERVER_PORT = 9933;
	
	public SnatchSFSClient(){
		this.initSmartFox();
	}
	
	public void connect(){
		System.out.println("Connecting to SFS");
		this.connectToServer(SERVER_HOSTNAME, SERVER_PORT);
	}
	
	private void initSmartFox()
	{
	    // Instantiate SmartFox client
	    sfsClient = new SmartFox(false);
	 
	    // Add event listeners
	    sfsClient.addEventListener(SFSEvent.CONNECTION, this);
	    sfsClient.addEventListener(SFSEvent.CONNECTION_LOST, this);
	    sfsClient.addEventListener(SFSEvent.LOGIN, this);
	    sfsClient.addEventListener(SFSEvent.ROOM_JOIN, this);
	    sfsClient.addEventListener(SFSEvent.HANDSHAKE, this);
	    sfsClient.addEventListener(SFSEvent.UDP_INIT, this);
	    sfsClient.addEventListener(SFSEvent.PING_PONG, this);

	}
	
	/**
	 * Frees the resources.
	 */
	public void destroy()
	{
		
		//Removes the event handlers and disconnect from the server.
		if(sfsClient != null)
		{
			sfsClient.removeEventListener(SFSEvent.CONNECTION, this);
			sfsClient.removeEventListener(SFSEvent.CONNECTION_LOST, this);
			sfsClient.removeEventListener(SFSEvent.LOGIN, this);
			sfsClient.removeEventListener(SFSEvent.ROOM_JOIN, this);
			sfsClient.removeEventListener(SFSEvent.HANDSHAKE, this);
			sfsClient.removeEventListener(SFSEvent.UDP_INIT, this);
			sfsClient.removeEventListener(SFSEvent.USER_ENTER_ROOM, this);
			sfsClient.removeEventListener(SFSEvent.USER_EXIT_ROOM, this);
			sfsClient.removeEventListener(SFSEvent.PUBLIC_MESSAGE, this);
	    	
			sfsClient.disconnect();
		}
		
	}
	public void shutdown(){
		this.destroy();
	}
	
	public void setComponent(MojamComponent mojamComponent) {
		this.mojamComponent = mojamComponent;
	}

	/**
	 * Connects to SmartFoxServer instance.
	 *
	 * @param ip the server IP.
	 * @param port the server port.
	 */
	private void connectToServer(final String ip, final int port)
	{
		System.out.println("Connecting...");
	    //connect() method is called in separate thread
	    //so it does not blocks the UI
	    final SmartFox sfs = sfsClient;
	    new Thread() {
	        @Override
	        public void run() {
	            sfs.connect(ip, port);
	        }
	    }.start();
	}
	
	/**
	 * Handle events dispatched from the SFS2X server
	 * @param event - the event that has been dispatched from the server
	 * 
	 * @throws SFSException
	 */
	@Override
	public void dispatch(final BaseEvent event) throws SFSException
	{

		if(event.getType().equalsIgnoreCase(SFSEvent.CONNECTION))
		{
			//if the connections is successful login dialog is shown
			if(event.getArguments().get("success").equals(true))
			{
				//removeDialog(DIALOG_CONNECTING_ID);
				//showDialog(DIALOG_LOGIN_ID);
				sfsClient.send(new LoginRequest("", "", SFS_ZONE));
			}
			//otherwise error message is shown
			else
			{
				//removeDialog(DIALOG_CONNECTING_ID);
				//showDialog(DIALOG_CONNECTION_ERROR_ID);
			}			    	
		}
		//When the connection is lost the UI is disabled and message is shown
		else if(event.getType().equalsIgnoreCase(SFSEvent.CONNECTION_LOST))
		{
			sfsClient.enableLagMonitor(false);
			sfsClient.disconnect();
			//resetUI();
			//showDialog(DIALOG_CONNECTION_LOST_ID);
		}
		else if(event.getType().equalsIgnoreCase(SFSEvent.LOGIN))
		{
			if (!sfsClient.isUdpInited() && sfsClient.isUdpAvailable()){
				sfsClient.initUdp(SERVER_HOSTNAME,SERVER_PORT);
			}
			sfsClient.enableLagMonitor(true);
			// Check if the "game" group is already subscribed; if not, subscribe it
			/*if (!sfsClient.getRoomManager().containsGroup(GAME_ROOMS_GROUP_NAME))
			{
				sfsClient.send(new SubscribeRoomGroupRequest(GAME_ROOMS_GROUP_NAME));
			}*/
			// Join The Lobby room
			sfsClient.send(new JoinRoomRequest("Lobby"));			    	
		}
		//if the login is not successful then error message and the login dialog are shown until it's successful
		else if(event.getType().equalsIgnoreCase(SFSEvent.LOGIN_ERROR))
		{
			mLoginError = event.getArguments().get("error").toString();
			//showDialog(DIALOG_LOGIN_ERROR_ID);
		}
		else if (event.getType().equalsIgnoreCase(SFSEvent.UDP_INIT)){
			if (event.getArguments().get("success").equals(true)){
				System.out.println("UDP connected.");
			}else{
				System.out.println("UDP failed.");
			}
		}
		else if (event.getType().equalsIgnoreCase(SFSEvent.PING_PONG)){
			latency = Integer.parseInt(event.getArguments().get("lagValue").toString());
			System.out.println("latency: "+event.getArguments().get("lagValue").toString());
			
		}
		//When the user joins new room then a message is added to the chat history and the UI is enabled
		else if(event.getType().equalsIgnoreCase(SFSEvent.ROOM_JOIN))
		{
			/*mUsers.clear();

			Room room = (Room)event.getArguments().get("room");
			for(User user : room.getUserList())
			{
				mUsers.add(user);
			}

			//If a game is joined set up a new game
			if (room.isGame())
			{
				mGameList.clear();
				List<Room> GameList = sfsClient.getRoomListFromGroup(GAME_ROOMS_GROUP_NAME);
				for(Room rm : GameList)
				{
					mGameList.add(rm.getName() + "         Users: " + rm.getUserCount() + "/" + rm.getMaxUsers());
				}
				//initGame();
			}
			else
			{
				//appendChatMessage("Room [" + room.getName() + "] joined\n");
				//tabHost.setCurrentTab(0);
				//Enables the chat UI
				//enableChatUI();
			}*/	            		
		}
		else if(event.getType().equalsIgnoreCase(SFSEvent.ROOM_JOIN_ERROR))
		{
			/*LayoutInflater inflater = getLayoutInflater();
			View layout = inflater.inflate(R.layout.toast_layout, (ViewGroup) findViewById(R.id.toast_layout_root));

			ImageView image = (ImageView) layout.findViewById(R.id.image);
			image.setImageResource(R.drawable.sfs_app_logo);
			TextView text = (TextView) layout.findViewById(R.id.text);
			text.setText("Game in progress! Choose again!!");

			Toast toast = new Toast(getApplicationContext());
			toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
			toast.setDuration(Toast.LENGTH_LONG);
			toast.setView(layout);
			toast.show();*/	            		
		}
		else if(event.getType().equalsIgnoreCase(SFSEvent.ROOM_ADD) || event.getType().equalsIgnoreCase(SFSEvent.ROOM_REMOVE))
		{
			updateGameList();
			Room thisRoom = (Room)event.getArguments().get("room");
			//appendChatMessage(event.getType() + ": [" + thisRoom.getName() + "] \n");		    		
			//Enables the chat UI
			//enableChatUI();		    			            		
		}
		//When a user enters a game room the list is updated
		else if(event.getType().equals(SFSEvent.USER_ENTER_ROOM))
		{
			User user = (User)event.getArguments().get("user");
			Room room = (Room)event.getArguments().get("room");
			//Log.v(DEBUG_TAG, event.getType());
			if (room.isGame())
			{
				mUsers.add(user);
				updateGameList();
			}		        	
		}
		//When a user exits a game room the list is updated
		else if(event.getType().equals(SFSEvent.USER_EXIT_ROOM))
		{
			String userName = (String)event.getArguments().get("userName");
			mUsers.remove(userName);
			User user = (User)event.getArguments().get("user");
			Room room = (Room)event.getArguments().get("room");
			if (room.isGame())
			{
				mUsers.add(user);
				updateGameList();
			}
		}
		//When public message is received it's added to the chat history
		else if(event.getType().equals(SFSEvent.PUBLIC_MESSAGE))
		{
			//Log.v(DEBUG_TAG, event.getArguments().toString());
			User sender = (User)event.getArguments().get("sender");
			String msg = event.getArguments().get("message").toString();
			//appendChatMessage("[" + sender.getName() + "]: " + msg + "\n");
		}

	}
	
	/**
	 * Update game list
	 */
	public void updateGameList()
	{
		mGameList.clear();
		List<Room> gameList = sfsClient.getRoomListFromGroup(GAME_ROOMS_GROUP_NAME);
		ListIterator<Room> gameRoomIterator = gameList.listIterator();
		while (gameRoomIterator.hasNext())
		{
			Room room = gameRoomIterator.next();
			//Add each room back into the adapter with player count
			mGameList.add(room.getName() + "         Users: " + room.getUserCount() + "/" + room.getMaxUsers());
		}
	}

	public int getLatency(){
		return latency;
	}
	
}
