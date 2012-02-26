package com.mojang.mojam.gui;

import java.awt.event.KeyEvent;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.Options;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Screen;

public class CharacterSelectionMenu extends GuiMenu {

	private CharacterButton selected;

	private int xOffset, yOffset;

	public CharacterSelectionMenu() {
		addButtons();
	}

	private void addButtons() {
		int gameWidth = MojamComponent.GAME_WIDTH;
		int gameHeight = MojamComponent.GAME_HEIGHT;
		xOffset = (gameWidth - (CharacterButton.WIDTH * 2 + 20)) / 2;
		yOffset = (gameHeight - (CharacterButton.HEIGHT * 2 + 20)) / 2;
		selected = (CharacterButton) addButton(new CharacterButton(TitleMenu.CHARACTER_BUTTON_ID,
				Art.LORD_LARD, Art.getPlayer(Art.LORD_LARD)[0][6], xOffset, yOffset));
		selected.setSelected(true);
		addButton(new CharacterButton(TitleMenu.CHARACTER_BUTTON_ID, Art.HERR_VON_SPECK,
				Art.getPlayer(Art.HERR_VON_SPECK)[0][2], xOffset + 20 + CharacterButton.WIDTH,
				yOffset));
		addButton(new CharacterButton(TitleMenu.CHARACTER_BUTTON_ID, Art.DUCHESS_DONUT,
				Art.getPlayer(Art.DUCHESS_DONUT)[0][6], xOffset, yOffset + 20
						+ CharacterButton.HEIGHT));
		addButton(new CharacterButton(TitleMenu.CHARACTER_BUTTON_ID, Art.COUNTESS_CRULLER,
				Art.getPlayer(Art.COUNTESS_CRULLER)[0][2], xOffset + 20 + CharacterButton.WIDTH,
				yOffset + 20 + CharacterButton.HEIGHT));
		if (Options.isCharacterIDset()) {
			selected.setSelected(false);
			for (ClickableComponent button : buttons) {
				CharacterButton charButton = (CharacterButton) button;
				if (charButton.getCharacterID() == Options.getCharacterID()) {
					selected = charButton;
					break;
				}
			}
			selected.setSelected(true);
		}
		addButton(new Button(TitleMenu.BACK_ID, MojamComponent.texts.getStatic("character.select"),
				(gameWidth - 128) / 2, yOffset + 2 * CharacterButton.HEIGHT + 20 + 30));
	}

	@Override
	public void render(Screen screen) {
		screen.blit(Art.emptyBackground, 0, 0);
		super.render(screen);
		Font.defaultFont().drawCentered(screen, MojamComponent.texts.getStatic("character.text"),
				MojamComponent.GAME_WIDTH / 2, yOffset - 24);
	}

	@Override
	public void buttonPressed(ClickableComponent button) {
		if (button instanceof CharacterButton) {
			selected.setSelected(false);
			selected = (CharacterButton) button;
			selected.setSelected(true);
		} else {
			Options.set(Options.CHARACTER_ID, selected.getCharacterID());
			Options.saveProperties();
			MojamComponent.instance.playerCharacter = selected.getCharacterID();
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyPressed(KeyEvent e) {}

	@Override
	public void keyReleased(KeyEvent e) {}

}