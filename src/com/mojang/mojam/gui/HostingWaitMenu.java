package com.mojang.mojam.gui;

import java.awt.event.KeyEvent;
import java.net.*;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.screen.Screen;

public class HostingWaitMenu extends GuiMenu {

	public HostingWaitMenu() {
		super();

		addButton(new Button(TitleMenu.CANCEL_JOIN_ID, "Cancel", 364, 335));
	}

	@Override
	public void render(Screen screen) {

		screen.clear(0);
		screen.blit(Art.emptyBackground, 0, 0);
		Font.draw(screen, MojamComponent.texts.waitingForClient(), 100, 100);
		try {
			InetAddress thisIp = InetAddress.getLocalHost();
			Font.draw(screen, "Your IP:" + thisIp.getHostAddress(), 100, 120);
		} catch (Exception e) {
			e.printStackTrace();
		}
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
