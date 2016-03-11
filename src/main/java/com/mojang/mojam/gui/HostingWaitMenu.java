package com.mojang.mojam.gui;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.downloader.Downloader;
import com.mojang.mojam.gui.components.Button;
import com.mojang.mojam.gui.components.ClickableComponent;
import com.mojang.mojam.gui.components.Font;
import com.mojang.mojam.screen.AbstractScreen;
import com.mojang.mojam.screen.Art;

import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;

public class HostingWaitMenu extends GuiMenu {

	public String myIpLAN;
	public String myIpWAN;
	private Button cancelButton;

	public HostingWaitMenu() {
		super();

		cancelButton = (Button) addButton(new Button(TitleMenu.CANCEL_JOIN_ID, MojamComponent.texts.getStatic("cancel"), 364, 335));

		searchIpLAN();
		searchIpWAN();
	}

	@Override
	public void render(AbstractScreen screen) {

		screen.clear(0);
		screen.blit(Art.emptyBackground, 0, 0);
		Font font = Font.defaultFont();
		font.draw(screen, MojamComponent.texts.getStatic("mp.waitingForClient"), 100, 100);
		font.draw(screen, MojamComponent.texts.getStatic("mp.localIP") + myIpLAN, 100, 120);
		font.draw(screen, MojamComponent.texts.getStatic("mp.externalIP") + myIpWAN, 100, 140);

		super.render(screen);
	}

	@Override
	public void buttonPressed(ClickableComponent button) {
		// nothing.
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			cancelButton.postClick();
		} else {
			super.keyPressed(e);
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// nothing
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// nothing
	}

	public void searchIpWAN() {
		System.setProperty("http.agent", Downloader.getUserAgent());
		URL whatismyip;
		try {
			whatismyip = new URL("https://catacombsnatch.net/ipcheck");
			BufferedReader in;
			try {
				in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
				myIpWAN = in.readLine();
			} catch (IOException e) {
				myIpWAN = "N/A";
			}
		} catch (MalformedURLException e) {
			myIpWAN = "N/A";
		}
	}

	public void searchIpLAN() {
		try {
			InetAddress thisIp = InetAddress.getLocalHost();
			myIpLAN = thisIp.getHostAddress();
		} catch (Exception e) {
			myIpLAN = "N/A";
		}
	}

}
