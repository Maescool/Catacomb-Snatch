package com.mojang.mojam.gui;

import java.awt.event.KeyEvent;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Screen;

public class WinMenu extends GuiMenu {
	private int selectedItem = 0;
	private final int gameWidth;
	private int winningPlayer;
	private int characterID;

	public WinMenu(int gameWidth, int gameHeight, int winningPlayer, int characterID) {
		super();
		this.winningPlayer = winningPlayer;
		this.gameWidth = gameWidth;

		addButton(new Button(TitleMenu.RETURN_TO_TITLESCREEN, "Ok", (gameWidth - 128) / 2, 200));
	}

	@Override
	public void render(Screen screen) {
		screen.clear(0);
		screen.blit(Art.gameOverScreen, 0, 0);

		Font.draw(screen, MojamComponent.texts.winCharacter(winningPlayer, characterID), 180, 160);

		super.render(screen);

		screen.blit(Art.getPlayer(characterID)[0][6], (gameWidth - 128) / 2 - 40, 190 + selectedItem * 40);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_E) {
			buttons.get(selectedItem).postClick();
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
	}

	@Override
	public void buttonPressed(ClickableComponent button) {
	}

}
