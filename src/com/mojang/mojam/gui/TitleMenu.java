package com.mojang.mojam.gui;

import java.awt.event.KeyEvent;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.level.DifficultyInformation;
import com.mojang.mojam.level.LevelInformation;
import com.mojang.mojam.level.gamemode.GameMode;
import com.mojang.mojam.level.gamemode.GameModeVanilla;
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
	public static final int IGNORE_ID = 1015;
	public static final int OPTIONS_ID = 1016;
    public static final int LEVELS_NEXT_PAGE_ID = 1017;
    public static final int LEVELS_PREVIOUS_PAGE_ID = 1018;

	public static final int FULLSCREEN_ID = 2000;
	public static final int FPS_ID = 2001;
	public static final int VOLUME = 2002;
    public static final int MUSIC = 2003;
    public static final int SOUND = 2004;
    public static final int CREATIVE_ID = 2005;
    public static final int ALTERNATIVE_ID = 2006;
	
	public static final int KEY_BINDINGS_ID = 3000;
	public static final int KEY_UP_ID = 3001;
	public static final int KEY_DOWN_ID = 3002;
	public static final int KEY_LEFT_ID = 3003;
	public static final int KEY_RIGHT_ID = 3004;
	public static final int KEY_SPRINT_ID = 3005;
	public static final int KEY_FIRE_ID = 3006;
	public static final int KEY_USE_ID = 3007;
	public static final int KEY_BUILD_ID = 3008;
	public static final int KEY_UPGRADE_ID = 3009;
	public static final int KEY_CHAT_ID = 3010;
	
	public static final int CREDITS_ID = 4000;
	public static final int CREDITS_TITLE_ID = 4001;
	public static final int CREDITS_TEXT_ID = 4002;
	
	public static final int CHARACTER_ID = 5000;
	public static final int CHARACTER_BUTTON_ID = 5001;
	
	public static LevelInformation level = null;
	public static GameMode defaultGameMode= new GameModeVanilla();
	public static DifficultyInformation difficulty = null;

	public static String ip = "";

	private int selectedItem = 0;
	private final int gameWidth;

	public TitleMenu(int gameWidth, int gameHeight) {
		super();
		this.gameWidth = gameWidth;
		int startY = 140;
		addButton(new Button(SELECT_LEVEL_ID, MojamComponent.texts.getStatic("titlemenu.start"),
				(gameWidth - 128) / 2, (startY += 30)));
		addButton(new Button(SELECT_HOST_LEVEL_ID,
				MojamComponent.texts.getStatic("titlemenu.host"), (gameWidth - 128) / 2,
				(startY += 30)));
		addButton(new Button(JOIN_GAME_ID, MojamComponent.texts.getStatic("titlemenu.join"),
				(gameWidth - 128) / 2, (startY += 30)));
		addButton(new Button(HOW_TO_PLAY, MojamComponent.texts.getStatic("titlemenu.help"),
				(gameWidth - 128) / 2, (startY += 30)));
		addButton(new Button(OPTIONS_ID, MojamComponent.texts.getStatic("titlemenu.options"),
				(gameWidth - 128) / 2, (startY += 30)));
		addButton(new Button(EXIT_GAME_ID, MojamComponent.texts.getStatic("titlemenu.exit"),
				(gameWidth - 128) / 2, (startY += 30)));
	}

	@Override
	public void render(Screen screen) {

		screen.clear(0);
		// screen.blit(Art.titles[1], 0, 10);
		screen.blit(Art.titleScreen, 0, 0);

		super.render(screen);

		screen.blit(Art.getLocalPlayerArt()[0][6], (gameWidth - 128) / 2 - 40, 160 + selectedItem * 30);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
			selectedItem--;
			if (selectedItem < 0) {
				selectedItem = buttons.size() - 1;
			}
		} else if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
			selectedItem++;
			if (selectedItem > buttons.size() - 1) {
				selectedItem = 0;
			}
		} else if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_E) {
			e.consume();
			buttons.get(selectedItem).postClick();
		} else if (e.getKeyCode() == KeyEvent.VK_F11) {
			MojamComponent.toggleFullscreen();
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {}

	@Override
	public void keyTyped(KeyEvent ke) {

	}

	@Override
	public void buttonPressed(ClickableComponent button) {
		// nothing
	}

}
