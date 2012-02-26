package com.mojang.mojam.gui;

import java.awt.event.KeyEvent;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.screen.Screen;
import com.mojang.mojam.screen.Art;

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
		Font.draw(screen, MojamComponent.texts.getStatic("mp.enterIP"), 100, 100);
		Font.draw(screen, TitleMenu.ip + "-", 100, 120);

		super.render(screen);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// Start on Enter, Cancel on Escape
		if ((e.getKeyChar() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_E) && TitleMenu.ip.length() > 0) {
			joinButton.postClick();
		}
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			cancelButton.postClick();
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
	}

	@Override
	public void keyTyped(KeyEvent e) {

		if (e.getKeyChar() == KeyEvent.VK_BACK_SPACE && TitleMenu.ip.length() > 0) {
			TitleMenu.ip = TitleMenu.ip.substring(0, TitleMenu.ip.length() - 1);
		} else if (Font.letters.indexOf(Character.toUpperCase(e.getKeyChar())) >= 0) {
			TitleMenu.ip += e.getKeyChar();
		}
	}
<<<<<<< HEAD
=======

	@Override
	public void buttonPressed(ClickableComponent button) {
		// nothing
	}

<<<<<<< HEAD
>>>>>>> parent of cd61150... Cleanups, JavaDoc updates and some minor refactoring
=======
	@Override
	public void buttonPressed(ClickableComponent button) {
		// nothing
	}

<<<<<<< HEAD
>>>>>>> parent of cd61150... Cleanups, JavaDoc updates and some minor refactoring
=======
	@Override
	public void buttonPressed(ClickableComponent button) {
		// nothing
	}

<<<<<<< HEAD
>>>>>>> parent of cd61150... Cleanups, JavaDoc updates and some minor refactoring
=======
	@Override
	public void buttonPressed(ClickableComponent button) {
		// nothing
	}

<<<<<<< HEAD
>>>>>>> parent of cd61150... Cleanups, JavaDoc updates and some minor refactoring
=======
	@Override
	public void buttonPressed(ClickableComponent button) {
		// nothing
	}

>>>>>>> parent of cd61150... Cleanups, JavaDoc updates and some minor refactoring
}
