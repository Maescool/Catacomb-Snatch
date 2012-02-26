package com.mojang.mojam.gui;

import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.screen.Screen;
import com.mojang.mojam.screen.Art;

/**
 * This menu is displayed while waiting for a client if this side is hosting a
 * multiplayer game
 */
public class HostingWaitMenu extends GuiMenu {

	public String myIpLAN;
	public String myIpWAN;
	private Button cancelButton;

	/**
	 * Constructor
	 */
	public HostingWaitMenu() {
		super();

		cancelButton = (Button) addButton(new Button(TitleMenu.CANCEL_JOIN_ID,
				MojamComponent.texts.getStatic("cancel"), 364, 335));

		searchIpLAN();
		searchIpWAN();
	}

	@Override
	public void render(Screen screen) {

		screen.clear(0);
		screen.blit(Art.emptyBackground, 0, 0);
		Font.draw(screen,
				MojamComponent.texts.getStatic("mp.waitingForClient"), 100, 100);

		Font.draw(screen, MojamComponent.texts.getStatic("mp.localIP")
				+ myIpLAN, 100, 120);
		Font.draw(screen, MojamComponent.texts.getStatic("mp.externalIP")
				+ myIpWAN, 100, 140);

		super.render(screen);
	}

	@Override
	public void buttonPressed(ClickableComponent button) {
		// nothing.
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// Cancel on Escape

		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			cancelButton.postClick();
		}
	}

	/**
	 * Get the WAN IP of this host
	 */
	public void searchIpWAN() {
		URL whatismyip;
		try {
			whatismyip = new URL(
					"http://automation.whatismyip.com/n09230945.asp");
			BufferedReader in;
			try {
				in = new BufferedReader(new InputStreamReader(
						whatismyip.openStream()));
				myIpWAN = in.readLine();
			} catch (IOException e) {
				myIpWAN = "N/A";
			}
		} catch (MalformedURLException e) {
			myIpWAN = "N/A";
		}
	}

	/**
	 * Get the LAN IP of this host
	 */
	public void searchIpLAN() {
		try {
			InetAddress thisIp = InetAddress.getLocalHost();
			myIpLAN = thisIp.getHostAddress();
		} catch (Exception e) {
			myIpLAN = "N/A";
		}
	}
}
