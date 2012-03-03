package com.mojang.mojam.gui;

import java.awt.event.KeyEvent;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Screen;

public class LocaleMenu extends GuiMenu {

	private Button back;
	private int gameWidth;
	private int gameHeight;
	private int textY;

	private boolean inGame;
	private Button enBtn = null;
	private Button deBtn = null;
	private Button esBtn = null;
	private Button frBtn = null;
	private Button inBtn = null;
	private Button itBtn = null;
	private Button nlBtn = null;
	private Button pt_brBtn = null;
	private Button ruBtn = null;
	private Button siBtn = null;
	private Button svBtn = null;

	public LocaleMenu(boolean inGame) {
		this.inGame = inGame;
		gameWidth = MojamComponent.GAME_WIDTH;
		gameHeight = MojamComponent.GAME_HEIGHT;
		int offset = 32;
		int left_xOffset = (int) ((gameWidth / 2) - (Button.BUTTON_WIDTH * 1.2));
		int right_xOffset = (int) ((gameWidth / 2) + (Button.BUTTON_WIDTH * 1.2 - (Button.BUTTON_WIDTH)));
		int xOffset = (gameWidth - Button.BUTTON_WIDTH) / 2;
		int yOffset = (gameHeight - (7 * offset + 20 + (offset * 2))) / 2;
		textY = yOffset;
		yOffset += offset;
		int TopYOffset = yOffset;
		enBtn = (Button) addButton(new Button(TitleMenu.LOCALE_EN_ID, MojamComponent.texts.getStatic("options.locale_en"), left_xOffset, (yOffset += offset)));
		deBtn = (Button) addButton(new Button(TitleMenu.LOCALE_DE_ID, MojamComponent.texts.getStatic("options.locale_de"), left_xOffset, (yOffset += offset)));
		esBtn = (Button) addButton(new Button(TitleMenu.LOCALE_ES_ID, MojamComponent.texts.getStatic("options.locale_es"), left_xOffset, (yOffset += offset)));
		frBtn = (Button) addButton(new Button(TitleMenu.LOCALE_FR_ID, MojamComponent.texts.getStatic("options.locale_fr"), left_xOffset, (yOffset += offset)));
		inBtn = (Button) addButton(new Button(TitleMenu.LOCALE_IN_ID, MojamComponent.texts.getStatic("options.locale_in"), left_xOffset, (yOffset += offset)));
		svBtn = (Button) addButton(new Button(TitleMenu.LOCALE_SV_ID, MojamComponent.texts.getStatic("options.locale_sv"), xOffset, (yOffset += offset)));
		yOffset = TopYOffset;
		itBtn = (Button) addButton(new Button(TitleMenu.LOCALE_IT_ID, MojamComponent.texts.getStatic("options.locale_it"), right_xOffset, (yOffset += offset)));
		nlBtn = (Button) addButton(new Button(TitleMenu.LOCALE_NL_ID, MojamComponent.texts.getStatic("options.locale_nl"), right_xOffset, (yOffset += offset)));
		pt_brBtn = (Button) addButton(new Button(TitleMenu.LOCALE_PT_BR_ID, MojamComponent.texts.getStatic("options.locale_pt_br"), right_xOffset, (yOffset += offset)));
		ruBtn = (Button) addButton(new Button(TitleMenu.LOCALE_RU_ID, MojamComponent.texts.getStatic("options.locale_ru"), right_xOffset, (yOffset += offset)));
		siBtn = (Button) addButton(new Button(TitleMenu.LOCALE_SI_ID, MojamComponent.texts.getStatic("options.locale_si"), right_xOffset, (yOffset += offset)));
		yOffset += offset;
		back = (Button) addButton(new Button(TitleMenu.BACK_ID, MojamComponent.texts.getStatic("back"), xOffset, (yOffset += offset) + 20));
	}

	@Override
	public void buttonPressed(ClickableComponent button) {
	}

	@Override
	public void render(Screen screen) {

		if (!inGame) {
			screen.blit(Art.background, 0, 0);
		} else {
			screen.alphaFill(0, 0, gameWidth, gameHeight, 0xff000000, 0x30);
		}

		super.render(screen);
		Font.defaultFont().draw(screen, MojamComponent.texts.getStatic("titlemenu.sound_and_video"), MojamComponent.GAME_WIDTH / 2, textY, Font.Align.CENTERED);
		screen.blit(Art.getLocalPlayerArt()[0][6], buttons.get(selectedItem).getX() - 40, buttons.get(selectedItem).getY() - 8);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	public void change_locale() {
		back.setLabel(MojamComponent.texts.getStatic("back"));
		enBtn.setLabel(MojamComponent.texts.getStatic("options.locale_en"));
		deBtn.setLabel(MojamComponent.texts.getStatic("options.locale_de"));
		esBtn.setLabel(MojamComponent.texts.getStatic("options.locale_es"));
		frBtn.setLabel(MojamComponent.texts.getStatic("options.locale_fr"));
		inBtn.setLabel(MojamComponent.texts.getStatic("options.locale_in"));
		itBtn.setLabel(MojamComponent.texts.getStatic("options.locale_it"));
		nlBtn.setLabel(MojamComponent.texts.getStatic("options.locale_nl"));
		pt_brBtn.setLabel(MojamComponent.texts.getStatic("options.locale_pt_br"));
		ruBtn.setLabel(MojamComponent.texts.getStatic("options.locale_ru"));
		siBtn.setLabel(MojamComponent.texts.getStatic("options.locale_si"));
		svBtn.setLabel(MojamComponent.texts.getStatic("options.locale_sv"));
	}
}
