package com.mojang.mojam.gui;

import java.awt.event.KeyEvent;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Screen;

public class PauseMenu extends GuiMenu {
	private final int gameWidth;
    private final int gameHeight;

	private Button resumeButton;

	public PauseMenu(int gameWidth, int gameHeight) {
		super();
		this.gameWidth = gameWidth;
        this.gameHeight = gameHeight;

		resumeButton = (Button) addButton(new Button(TitleMenu.RETURN_ID,
				MojamComponent.texts.getStatic("pausemenu.resume"), (gameWidth - 128) / 2, 140));
		addButton(new Button(TitleMenu.HOW_TO_PLAY, MojamComponent.texts.getStatic("pausemenu.help"),
				(gameWidth - 128) / 2, 170));
		addButton(new Button(TitleMenu.OPTIONS_ID, MojamComponent.texts.getStatic("titlemenu.options"),
				(gameWidth - 128) / 2, 200));
		addButton(new Button(TitleMenu.RETURN_TO_TITLESCREEN, MojamComponent.texts.getStatic("pausemenu.backtomain"),
				(gameWidth - 128) / 2, 230));
		addButton(new Button(TitleMenu.EXIT_GAME_ID, MojamComponent.texts.getStatic("pausemenu.exit"),
				(gameWidth - 128) / 2, 260));

	}

	public void render(Screen screen) {

		//screen.clear(0);
		//screen.blit(Art.emptyBackground, 0, 0);
	    screen.alphaFill(0, 0, gameWidth, gameHeight, 0xff000000, 0x30);
		screen.blit(Art.pauseScreen, 0, 0);

		super.render(screen);

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
	
	public void keyReleased(KeyEvent arg0) {
	}

	public void keyTyped(KeyEvent arg0) {
	}

	@Override
	public void buttonPressed(ClickableComponent button) {
		// TODO Auto-generated method stub

	}

}
