package com.mojang.mojam.gui;

import java.awt.event.KeyEvent;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.gui.components.Button;
import com.mojang.mojam.gui.components.ClickableComponent;
import com.mojang.mojam.gui.components.Font;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.AbstractScreen;

public class LanMenu extends GuiMenu {
	private final int gameWidth;
    private final int gameHeight;

	private Button select_host_lvl = null;
	private Button join_host = null;
	private Button cancel = null;

	public void changeLocale() {
		select_host_lvl.setLabel(MojamComponent.texts.getStatic("titlemenu.host"));
		join_host.setLabel(MojamComponent.texts.getStatic("titlemenu.join"));
		cancel.setLabel(MojamComponent.texts.getStatic("cancel"));
	}

	public LanMenu(int gameWidth, int gameHeight) {
		super();
		this.gameWidth = gameWidth;
		this.gameHeight = gameHeight;

		select_host_lvl = (Button) addButton(new Button(TitleMenu.SELECT_HOST_LEVEL_ID, MojamComponent.texts.getStatic("titlemenu.host"), (gameWidth - 128) / 2, 140));
        join_host = (Button) addButton(new Button(TitleMenu.JOIN_GAME_ID, MojamComponent.texts.getStatic("titlemenu.join"), (gameWidth - 128) / 2, 170));
        cancel = (Button) addButton(new Button(TitleMenu.BACK_ID, MojamComponent.texts.getStatic("cancel"), (gameWidth - 128) / 2, 200));
	}

	public void render(AbstractScreen screen) {

		screen.blit(Art.emptyBackground, 0, 0);

		super.render(screen);
		
		int yOffset = (gameHeight - (CharacterButton.HEIGHT * 2 + 20)) / 2 - 20;
		Font.defaultFont().draw(screen, MojamComponent.texts.getStatic("lan.headline"),
				MojamComponent.GAME_WIDTH / 2, yOffset - 24, Font.Align.CENTERED);

		screen.blit(Art.getLocalPlayerArt()[0][6], (gameWidth - 128) / 2 - 40,
				130 + selectedItem * 30);
	}
	
	
	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			cancel.postClick();
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
