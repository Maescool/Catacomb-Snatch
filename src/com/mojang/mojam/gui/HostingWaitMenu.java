package com.mojang.mojam.gui;

import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

import com.mojang.mojam.screen.Screen;

public class HostingWaitMenu extends GuiMenu {

	public String myIpLAN;
	public String myIpWAN;
	public HostingWaitMenu() {
		super();
		addButton(new Button(TitleMenu.CANCEL_JOIN_ID, "Cancel", 250, 180));
	}
	public void searchIpWAN(){
		URL whatismyip;
		try {
			whatismyip = new URL("http://automation.whatismyip.com/n09230945.asp");
			BufferedReader in;
			try {
				in = new BufferedReader(new InputStreamReader(
				whatismyip.openStream()));
				myIpWAN = in.readLine(); 
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	public void searchIpLAN(){
		try {
			InetAddress thisIp = InetAddress.getLocalHost();
			myIpLAN = thisIp.getHostAddress();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	public void render(Screen screen) {
		screen.clear(0);
		if(myIpLAN!=null){
			Font.draw(screen, "Your ip LAN :"+myIpLAN, 100, 120);
		}else{
			searchIpLAN();
		}
		if(myIpWAN!=null){
			Font.draw(screen, "Your ip WAN :"+myIpWAN, 100, 140);
		}else{
			searchIpWAN();
		}
		Font.draw(screen, "Waiting for client to join...", 100, 100);
		
		super.render(screen);
	}

	@Override
	public void buttonPressed(ClickableComponent button) {
		// nothing.
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		// nothing
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// nothing
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// nothing
	}

}
