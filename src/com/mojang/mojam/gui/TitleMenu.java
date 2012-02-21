package com.mojang.mojam.gui;

import java.awt.event.KeyEvent;

import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Screen;

public class TitleMenu extends GuiMenu {

	public static final int START_GAME_ID = 1000;
	public static final int HOST_GAME_ID = 1002;
	public static final int JOIN_GAME_ID = 1003;
	public static final int EXIT_GAME_ID = 1001;

	public static final int CANCEL_JOIN_ID = 1004;
	public static final int PERFORM_JOIN_ID = 1005;
	public static final int RESTART_GAME_ID = 1006;
	public static final int SELECT_LEVEL_ID = 1007;
	public static final int SELECT_HOST_LEVEL_ID = 1008;
	public static final int SELECT_DIFFICULTY_ID = 1009;

	public static String level = "";
	public static int Difficulty = 0;

	// public static lol... ;)
	public static String ip = "";

	private int selectedItem = 0;
	private final int gameWidth;

	public TitleMenu(int gameWidth, int gameHeight) {
		super();
		this.gameWidth = gameWidth;

		addButton(new Button(SELECT_LEVEL_ID, "start", true, (gameWidth - 128) / 2, 200));
		addButton(new Button(SELECT_HOST_LEVEL_ID, "host", true, (gameWidth - 128) / 2,
				240));
		addButton(new Button(JOIN_GAME_ID, "join", true, (gameWidth - 128) / 2, 280));
		addButton(new Button(EXIT_GAME_ID, "exit", true, (gameWidth - 128) / 2, 320));
	}

	public void render(Screen screen) {

		screen.clear(0);
		// screen.blit(Art.titles[1], 0, 10);
		screen.blit(Art.titleScreen, 0, 0);

		super.render(screen);

		screen.blit(Art.lordLard[0][6], (gameWidth - 128) / 2 - 40,
				190 + selectedItem * 40);
	}

	@Override
	public void buttonPressed(Button button) {
	}

	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_UP) {
			selectedItem--;
			if (selectedItem < 0) {
				selectedItem = 3;
			}
		} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			selectedItem++;
			if (selectedItem > 3) {
				selectedItem = 0;
			}
		} else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			e.consume();
			buttons.get(selectedItem).postClick();
		}
	}

	public void keyReleased(KeyEvent arg0) {
	}

	public void keyTyped(KeyEvent arg0) {
	}

}
