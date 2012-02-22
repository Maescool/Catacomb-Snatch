package com.mojang.mojam.gui;

import java.awt.event.KeyEvent;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.screen.*;

public class WinMenu extends GuiMenu {
	private int selectedItem = 0;
	private final int gameWidth;
	private int winningPlayer;

	public WinMenu(int gameWidth, int gameHeight, int winningPlayer) {
		super();
		this.winningPlayer = winningPlayer;
		this.gameWidth = gameWidth;

		addButton(new Button(TitleMenu.RESTART_GAME_ID, 1,
				(gameWidth - 128) / 2, 200));
	}

	public void render(Screen screen) {
		screen.clear(0);
		screen.blit(Art.gameOverScreen, 0, 0);

		String msg = "";
		if (winningPlayer == 1)
			msg = MojamComponent.texts.player1Win();
		if (winningPlayer == 2)
			msg = MojamComponent.texts.player2Win();
		Font.draw(screen, msg, 180, 160);

		super.render(screen);

		if (winningPlayer == 1)
			screen.blit(Art.lordLard[0][6], (gameWidth - 128) / 2 - 40,
					190 + selectedItem * 40);
		if (winningPlayer == 2)
			screen.blit(Art.herrSpeck[0][6], (gameWidth - 128) / 2 - 40,
					190 + selectedItem * 40);
	}

	@Override
	public void buttonPressed(Button button) {
	}

	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			buttons.get(selectedItem).postClick();
		}
	}

	public void keyReleased(KeyEvent arg0) {
	}

	public void keyTyped(KeyEvent arg0) {
	}

}
