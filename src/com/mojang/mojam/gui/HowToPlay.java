package com.mojang.mojam.gui;

import java.awt.event.KeyEvent;

import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Screen;

public class HowToPlay extends GuiMenu {

	public HowToPlay(int gameWidth, int gameHeight) {
		addButton(new TextButton(TitleMenu.HOW_TO_PLAY_BACK, "back", gameWidth - 128 - 20,
				gameHeight - 24 - 25));
	}

	public void render(Screen screen) {
		screen.blit(Art.howToPlayScreen, 0, 0);
		super.render(screen);
	}

	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyPressed(KeyEvent e) {}

	@Override
	public void keyReleased(KeyEvent e) {}

}
