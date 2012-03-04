package com.mojang.mojam.gui;

import java.awt.event.KeyEvent;
import java.util.Locale;

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
	public static final int LEVEL_EDITOR_ID = 1019;
	public static final int AUDIO_VIDEO_ID = 1020;
	public static final int LOCALE_ID = 1021;

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
	public static final int KEY_FIRE_UP_ID = 3011;
	public static final int KEY_FIRE_DOWN_ID = 3012;
	public static final int KEY_FIRE_LEFT_ID = 3013;
	public static final int KEY_FIRE_RIGHT_ID = 3014;

	public static final int CREDITS_ID = 4000;
	public static final int CREDITS_TITLE_ID = 4001;
	public static final int CREDITS_TEXT_ID = 4002;

	public static final int CHARACTER_ID = 5000;
	public static final int CHARACTER_BUTTON_ID = 5001;

	public static final int LOCALE_EN_ID = 6001;
	public static final int LOCALE_DE_ID = 6002;
	public static final int LOCALE_ES_ID = 6003;
	public static final int LOCALE_FR_ID = 6004;
	public static final int LOCALE_ID_ID = 6005;
	public static final int LOCALE_IT_ID = 6006;
	public static final int LOCALE_NL_ID = 6007;
	public static final int LOCALE_PT_BR_ID = 6008;
	public static final int LOCALE_RU_ID = 6009;
	public static final int LOCALE_SL_ID = 6010;
	public static final int LOCALE_SV_ID = 6011;
	public static final int LOCALE_AF_ID = 6012;

	public static LevelInformation level = null;
	public static GameMode defaultGameMode = new GameModeVanilla();
	public static DifficultyInformation difficulty = null;

	public static String ip = "";

	private final int gameWidth;
	private final int gameHeight;

	private Button select_lvl_btn = null;
	private Button select_host_lvl = null;
	private Button join_host = null;
	private Button how_to = null;
	private Button options = null;
	private Button lvl_editor = null;
	private Button exit_game = null;

	public TitleMenu(int gameWidth, int gameHeight) {
		super();
		this.gameWidth = gameWidth;
		this.gameHeight = gameHeight;
		int startY = 130;
		try {
			select_lvl_btn = (Button) addButton(new Button(SELECT_LEVEL_ID, MojamComponent.texts.getStatic("titlemenu.start"), (gameWidth - 128) / 2, (startY += 30)));
			select_host_lvl = (Button) addButton(new Button(SELECT_HOST_LEVEL_ID, MojamComponent.texts.getStatic("titlemenu.host"), (gameWidth - 128) / 2, (startY += 30)));
			join_host = (Button) addButton(new Button(JOIN_GAME_ID, MojamComponent.texts.getStatic("titlemenu.join"), (gameWidth - 128) / 2, (startY += 30)));
			how_to = (Button) addButton(new Button(HOW_TO_PLAY, MojamComponent.texts.getStatic("titlemenu.help"), (gameWidth - 128) / 2, (startY += 30)));
			options = (Button) addButton(new Button(OPTIONS_ID, MojamComponent.texts.getStatic("titlemenu.options"), (gameWidth - 128) / 2, (startY += 30)));
			lvl_editor = (Button) addButton(new Button(LEVEL_EDITOR_ID, MojamComponent.texts.getStatic("titlemenu.levelEditor"), (gameWidth - 128) / 2, (startY += 30)));
			exit_game = (Button) addButton(new Button(EXIT_GAME_ID, MojamComponent.texts.getStatic("titlemenu.exit"), (gameWidth - 128) / 2, (startY += 30)));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void render(Screen screen) {

		screen.clear(0);
		// screen.blit(Art.titles[1], 0, 10);
		screen.blit(Art.titleScreen, 0, 0);

		super.render(screen);

		screen.blit(Art.getLocalPlayerArt()[0][6], (gameWidth - 128) / 2 - 40, 150 + selectedItem * 30);

		// Display version number
		Font.FONT_GOLD_SMALL.draw(screen, MojamComponent.GAME_VERSION, gameWidth - 10, gameHeight - 10, Font.Align.RIGHT);
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

	public void change_locale() {
		System.out.println("Called");
		select_lvl_btn.setLabel(MojamComponent.texts.getStatic(("titlemenu.start")));
		select_host_lvl.setLabel(MojamComponent.texts.getStatic(("titlemenu.host")));
		join_host.setLabel(MojamComponent.texts.getStatic(("titlemenu.join")));
		how_to.setLabel(MojamComponent.texts.getStatic(("titlemenu.help")));
		options.setLabel(MojamComponent.texts.getStatic(("titlemenu.options")));
		lvl_editor.setLabel(MojamComponent.texts.getStatic(("titlemenu.levelEditor")));
		exit_game.setLabel(MojamComponent.texts.getStatic(("titlemenu.exit")));
	}
}
