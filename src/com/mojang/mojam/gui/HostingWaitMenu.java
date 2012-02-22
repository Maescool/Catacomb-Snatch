package com.mojang.mojam.gui;

import java.awt.event.KeyEvent;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.screen.Screen;

public class HostingWaitMenu extends GuiMenu {

	public HostingWaitMenu() {
		super();

		addButton(new Button(TitleMenu.CANCEL_JOIN_ID, 4, 250, 180));
	}

	@Override
	public void render(Screen screen) {

		screen.clear(0);
		Font.draw(screen, MojamComponent.texts.waitingForClient(), 100, 100);

		super.render(screen);
	}

	public void keyPressed(KeyEvent arg0) {
	}

	public void keyReleased(KeyEvent arg0) {
	}

	public void keyTyped(KeyEvent arg0) {
	}

}
