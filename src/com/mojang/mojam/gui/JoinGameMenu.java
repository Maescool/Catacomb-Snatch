package com.mojang.mojam.gui;

import java.awt.event.KeyEvent;

import com.mojang.mojam.screen.Screen;

public class JoinGameMenu extends GuiMenu {

	private Button joinButton;

	public JoinGameMenu() {
		super();

		joinButton = addButton(new Button(TitleMenu.PERFORM_JOIN_ID, 3, 100,
				180));
		addButton(new Button(TitleMenu.CANCEL_JOIN_ID, 4, 250, 180));
	}

	@Override
	public void render(Screen screen) {

		screen.clear(0);
		Font.draw(screen, "Enter IP of Host:", 100, 100);
		Font.draw(screen, TitleMenu.ip + "-", 100, 120);

		super.render(screen);
	}

	public void keyPressed(KeyEvent e) {
		if (e.getKeyChar() == KeyEvent.VK_ENTER && TitleMenu.ip.length() > 0) {
			joinButton.postClick();
		}
	}

	public void keyReleased(KeyEvent arg0) {
	}

	public void keyTyped(KeyEvent e) {

		if (e.getKeyChar() == KeyEvent.VK_BACK_SPACE
				&& TitleMenu.ip.length() > 0) {
			TitleMenu.ip = TitleMenu.ip.substring(0, TitleMenu.ip.length() - 1);
		} else if (Font.letters.indexOf(Character.toUpperCase(e.getKeyChar())) >= 0) {
			TitleMenu.ip += e.getKeyChar();
		}
	}

}
