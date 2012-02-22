package com.mojang.mojam.gui;

import java.awt.event.KeyEvent;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Screen;

public class HowToPlay extends GuiMenu {

	public HowToPlay() {
		addButton(new Button(TitleMenu.RETURN_TO_TITLESCREEN, "back", MojamComponent.GAME_WIDTH - 128 - 20, MojamComponent.GAME_HEIGHT - 24 - 25));
	}

	public void render(Screen screen) {
		screen.blit(Art.howToPlayScreen, 0, 0);
		super.render(screen);
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			buttons.get(0).postClick();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	public void buttonPressed(ClickableComponent button) {
	}

}
