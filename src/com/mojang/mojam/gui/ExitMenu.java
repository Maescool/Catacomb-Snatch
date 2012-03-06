package com.mojang.mojam.gui;

import java.awt.event.KeyEvent;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Screen;

public class ExitMenu extends GuiMenu {
	private final int gameWidth;
    private final int gameHeight;

	private Button resumeButton = null;
	private Button exit_game = null;

	public void changeLocale() {
		resumeButton.setLabel(MojamComponent.texts.getStatic("exitmenu.cancel"));
		exit_game.setLabel(MojamComponent.texts.getStatic("exitmenu.exit"));
	}

	public ExitMenu(int gameWidth, int gameHeight) {
		super();
		this.gameWidth = gameWidth;
		this.gameHeight = gameHeight;

		resumeButton = (Button) addButton(new Button(TitleMenu.BACK_ID, MojamComponent.texts.getStatic("exitmenu.cancel"), (gameWidth - 128) / 2, 140));
		exit_game = (Button) addButton(new Button(TitleMenu.REALLY_EXIT_GAME_ID, MojamComponent.texts.getStatic("exitmenu.exit"), (gameWidth - 128) / 2, 170));

	}

	public void render(Screen screen) {

		screen.blit(Art.emptyBackground, 0, 0);

		super.render(screen);
		
		int yOffset = (gameHeight - (CharacterButton.HEIGHT * 2 + 20)) / 2 - 20;
		Font.defaultFont().draw(screen, MojamComponent.texts.getStatic("exitmenu.headline"),
				MojamComponent.GAME_WIDTH / 2, yOffset - 24, Font.Align.CENTERED);

		screen.blit(Art.getLocalPlayerArt()[0][6], (gameWidth - 128) / 2 - 40,
				130 + selectedItem * 30);
	}
	
	
	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			resumeButton.postClick();
		} else {
			super.keyPressed(e);
		}		
	}

	@Override
	public void buttonPressed(ClickableComponent button) {}

	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyReleased(KeyEvent e) {}
}
