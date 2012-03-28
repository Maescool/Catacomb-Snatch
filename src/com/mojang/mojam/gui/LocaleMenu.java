package com.mojang.mojam.gui;

import java.awt.event.KeyEvent;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.gui.components.Button;
import com.mojang.mojam.gui.components.ClickableComponent;
import com.mojang.mojam.gui.components.Font;
import com.mojang.mojam.resources.Constants;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Screen;

public class LocaleMenu extends GuiMenu {

	private Button back;
	private int gameWidth;
	private int gameHeight;
	private int textY;
	private String confirm = MojamComponent.texts.getStatic("back");

	private boolean inGame;
	private Button enBtn = null;
	private Button deBtn = null;
	private Button esBtn = null;
	private Button frBtn = null;
	private Button idBtn = null;
	private Button itBtn = null;
	private Button nlBtn = null;
	private Button plBtn = null;
	private Button pt_brBtn = null;
	private Button ruBtn = null;
	private Button slBtn = null;
	private Button svBtn = null;
	private Button afBtn = null;

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
		enBtn = (Button) addButton(new Button(TitleMenu.LOCALE_EN_ID, Constants.getString("options.locale_en"), left_xOffset, (yOffset += offset)));
		deBtn = (Button) addButton(new Button(TitleMenu.LOCALE_DE_ID, Constants.getString("options.locale_de"), left_xOffset, (yOffset += offset)));
		esBtn = (Button) addButton(new Button(TitleMenu.LOCALE_ES_ID, Constants.getString("options.locale_es"), left_xOffset, (yOffset += offset)));
		frBtn = (Button) addButton(new Button(TitleMenu.LOCALE_FR_ID, Constants.getString("options.locale_fr"), left_xOffset, (yOffset += offset)));
		idBtn = (Button) addButton(new Button(TitleMenu.LOCALE_IND_ID, Constants.getString("options.locale_ind"), left_xOffset, (yOffset += offset)));
		svBtn = (Button) addButton(new Button(TitleMenu.LOCALE_SV_ID, Constants.getString("options.locale_sv"), left_xOffset, (yOffset += offset)));
		itBtn = (Button) addButton(new Button(TitleMenu.LOCALE_IT_ID, Constants.getString("options.locale_it"), left_xOffset, (yOffset += offset)));
		yOffset = TopYOffset;
		nlBtn = (Button) addButton(new Button(TitleMenu.LOCALE_NL_ID, Constants.getString("options.locale_nl"), right_xOffset, (yOffset += offset)));
		plBtn = (Button) addButton(new Button(TitleMenu.LOCALE_PL_ID, Constants.getString("options.locale_pl"), right_xOffset, (yOffset += offset)));
		pt_brBtn = (Button) addButton(new Button(TitleMenu.LOCALE_PT_BR_ID, Constants.getString("options.locale_pt_br"), right_xOffset, (yOffset += offset)));
		ruBtn = (Button) addButton(new Button(TitleMenu.LOCALE_RU_ID, Constants.getString("options.locale_ru"), right_xOffset, (yOffset += offset)));
		slBtn = (Button) addButton(new Button(TitleMenu.LOCALE_SL_ID, Constants.getString("options.locale_sl"), right_xOffset, (yOffset += offset)));
		afBtn = (Button) addButton(new Button(TitleMenu.LOCALE_AF_ID, Constants.getString("options.locale_af"), right_xOffset, (yOffset += offset)));
		yOffset += offset;
		back = (Button) addButton(new Button(TitleMenu.BACK_ID, MojamComponent.texts.getStatic("back"), xOffset, (yOffset += offset) + 20));
	}
	
	public LocaleMenu(String s) {
		this(false);
		confirm = MojamComponent.texts.getStatic(s);
		changeLocale();
	}

	@Override
	public void buttonPressed(ClickableComponent button) {
	}

	@Override
	public void render(Screen screen) {

		if (!inGame) {
			screen.blit(Art.background, 0, 0);
		} else {
			screen.alphaFill(0, 0, gameWidth, gameHeight, 0xff000000, 0xC0);
		}

		super.render(screen);
		Font.defaultFont().draw(screen, MojamComponent.texts.getStatic("options.locale_selection"), MojamComponent.GAME_WIDTH / 2, textY, Font.Align.CENTERED);
		screen.blit(Art.getLocalPlayerArt()[0][6], buttons.get(selectedItem).getX() - 40, buttons.get(selectedItem).getY() - 8);
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}
	
	@Override
	public void keyPressed(KeyEvent e){
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
			back.postClick();
		} else {
			super.keyPressed(e);
		}
	}

	public void changeLocale() {
		back.setLabel(confirm);
		enBtn.setLabel(Constants.getString("options.locale_en"));
		deBtn.setLabel(Constants.getString("options.locale_de"));
		esBtn.setLabel(Constants.getString("options.locale_es"));
		frBtn.setLabel(Constants.getString("options.locale_fr"));
		idBtn.setLabel(Constants.getString("options.locale_ind"));
		itBtn.setLabel(Constants.getString("options.locale_it"));
		nlBtn.setLabel(Constants.getString("options.locale_nl"));
		plBtn.setLabel(Constants.getString("options.locale_pl"));
		pt_brBtn.setLabel(Constants.getString("options.locale_pt_br"));
		ruBtn.setLabel(Constants.getString("options.locale_ru"));
		slBtn.setLabel(Constants.getString("options.locale_sl"));
		svBtn.setLabel(Constants.getString("options.locale_sv"));
		afBtn.setLabel(Constants.getString("options.locale_af"));
	}
}
