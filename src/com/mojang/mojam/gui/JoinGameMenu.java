package com.mojang.mojam.gui;

import java.awt.event.KeyEvent;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Screen;

public class JoinGameMenu extends GuiMenu {

	private Button joinButton;
	private Button cancelButton;

	public JoinGameMenu() {
		super();

		joinButton = (Button) addButton(new Button(TitleMenu.PERFORM_JOIN_ID, MojamComponent.texts.getStatic("mp.join"), 100, 180));
		cancelButton = (Button) addButton(new Button(TitleMenu.CANCEL_JOIN_ID, MojamComponent.texts.getStatic("cancel"), 250, 180));
	}

	@Override
	public void render(Screen screen) {

		screen.clear(0);
		screen.blit(Art.emptyBackground, 0, 0);
		Font.defaultFont().draw(screen, MojamComponent.texts.getStatic("mp.enterIP"), 100, 100);
		Font.defaultFont().draw(screen, TitleMenu.ip + "-", 100, 120);

		super.render(screen);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// Start on Enter, Cancel on Escape
		if ((e.getKeyChar() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_E)) {
			if (TitleMenu.ip.length() > 0) {
				joinButton.postClick();
			}
		} else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			cancelButton.postClick();	
		} else {
			super.keyPressed(e);
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
	}

	@Override
	public void keyTyped(KeyEvent e) {

		if (e.getKeyChar() == KeyEvent.VK_BACK_SPACE && TitleMenu.ip.length() > 0) {
			TitleMenu.ip = TitleMenu.ip.substring(0, TitleMenu.ip.length() - 1);
		} else {
			TitleMenu.ip += e.getKeyChar();
		}
	}

	@Override
	public void buttonPressed(ClickableComponent button) {
		// nothing
	}

}
