package com.mojang.mojam.gui;

import java.awt.event.KeyEvent;

import com.mojang.mojam.level.LevelInformation;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Screen;

public class TitleMenu extends GuiMenu {

	public static final int START_GAME_ID = 1000;
	public static final int HOST_GAME_ID = 1002;
	public static final int JOIN_GAME_ID = 1003;
	public static final int EXIT_GAME_ID = 1001;
	public static final int CANCEL_JOIN_ID = 1004;
	public static final int PERFORM_JOIN_ID = 1005;
	public static final int RETURN_TO_TITLESCREEN = 1006;
	public static final int SELECT_LEVEL_ID = 1007;
	public static final int SELECT_HOST_LEVEL_ID = 1008;
	public static final int SELECT_DIFFICULTY_ID = 1009;
	public static final int UPDATE_LEVELS = 1011;
	public static final int SHOW_HELP = 1010;
	
	public static int TOP_MARGIN = 180;
	public static int BUTTON_HEIGHT = 30;

	public static LevelInformation level = null;
	public static int Difficulty = 0;
	
	// public static lol... ;)
	public static String ip = "";

	private int selectedItem = 0;
	private final int gameWidth;

	public TitleMenu(int gameWidth, int gameHeight) {
		super();
		this.gameWidth = gameWidth;

		addButton(new Button(SELECT_LEVEL_ID, "Start", (gameWidth - 128) / 2, TOP_MARGIN+buttons.size()*BUTTON_HEIGHT));
		addButton(new Button(SELECT_HOST_LEVEL_ID, "Host", (gameWidth - 128) / 2,
				TOP_MARGIN+buttons.size()*BUTTON_HEIGHT));
		addButton(new Button(JOIN_GAME_ID, "Join", (gameWidth - 128) / 2, TOP_MARGIN+buttons.size()*BUTTON_HEIGHT));
		addButton(new Button(SHOW_HELP, "Help", (gameWidth - 128) / 2, TOP_MARGIN+buttons.size()*BUTTON_HEIGHT));
		addButton(new Button(EXIT_GAME_ID, "Exit", (gameWidth - 128) / 2, TOP_MARGIN+buttons.size()*BUTTON_HEIGHT));

	}

	@Override
	public void render(Screen screen) {

		screen.clear(0);
		// screen.blit(Art.titles[1], 0, 10);
		screen.blit(Art.titleScreen, 0, 0);

		super.render(screen);

		screen.blit(Art.lordLard[0][6], (gameWidth - 128) / 2 - 40,
				TOP_MARGIN-10 + selectedItem * BUTTON_HEIGHT);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_UP) {
			selectedItem--;
			if (selectedItem < 0) {
				selectedItem = buttons.size()-1;
			}
		} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			selectedItem++;
			if (selectedItem > buttons.size()-1) {
				selectedItem = 0;
			}
		} else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			e.consume();
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
		// nothing
	}

}
