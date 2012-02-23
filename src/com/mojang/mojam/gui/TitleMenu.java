package com.mojang.mojam.gui;

import java.awt.event.KeyEvent;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.level.DifficultyInformation;
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
	public static final int HOW_TO_PLAY = 1010;
	public static final int UPDATE_LEVELS = 1011;
	public static final int RETURN_ID = 1012;
	public static final int SELECT_DIFFICULTY_HOSTING_ID = 1013;
	public static final int BACK_ID = 1014;
	public static final int SAVE_LEVEL = 1015;
	
	public static LevelInformation level = null;
	public static DifficultyInformation difficulty = null;

	// public static lol... ;)
	public static String ip = "";
	

	private int selectedItem = 0;
	private final int gameWidth;

	public TitleMenu(int gameWidth, int gameHeight) {
		super();
		this.gameWidth = gameWidth;

		addButton(new Button(SELECT_LEVEL_ID, "Start", (gameWidth - 128) / 2, 200));
		addButton(new Button(SELECT_HOST_LEVEL_ID, "Host", (gameWidth - 128) / 2, 230));
		addButton(new Button(JOIN_GAME_ID, "Join", (gameWidth - 128) / 2, 260));
		addButton(new Button(HOW_TO_PLAY, "How to play", (gameWidth - 128) / 2, 290));
		addButton(new Button(EXIT_GAME_ID, "Exit", (gameWidth - 128) / 2, 320));
	}

	@Override
	public void render(Screen screen) {

		screen.clear(0);
		// screen.blit(Art.titles[1], 0, 10);
		screen.blit(Art.titleScreen, 0, 0);

		super.render(screen);

		screen.blit(Art.lordLard[0][6], (gameWidth - 128) / 2 - 40, 190 + selectedItem * 30);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_UP) {
			selectedItem--;
			if (selectedItem < 0) {
				selectedItem = 4;
			}
		} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			selectedItem++;
			if (selectedItem > 4) {
				selectedItem = 0;
			}
		} else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			e.consume();
			buttons.get(selectedItem).postClick();
		} else if (e.getKeyCode() == KeyEvent.VK_F11) {
			MojamComponent.setFullscreen(!MojamComponent.isFulscreen());
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
	}

	@Override
	public void keyTyped(KeyEvent ke) {
		
	}

	@Override
	public void buttonPressed(ClickableComponent button) {
		// nothing
	}

}
