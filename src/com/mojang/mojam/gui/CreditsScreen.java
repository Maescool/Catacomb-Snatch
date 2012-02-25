package com.mojang.mojam.gui;

import java.awt.event.KeyEvent;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.Options;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Screen;

public class CreditsScreen extends GuiMenu {
	private final int gameWidth;
	private final int gameHeight;

	private ClickableComponent back;
	public String text = "Project team\n"
			+ "Leader: @Maescool\n"
			+ "Original game: Mojang AB\n"
			+ "Official developers: @Borsty, @danielduner, @flet, @judgedead53, @Maescool, @master-lincoln, @mkalam-alami, @Scorpion1122\n"
			+ "Community management: @Austin01 (website manager, webmaster), @zorro300 (MCForums thread)\n"
			+ "Other contributors:\nhttps://github.com/Maescool/Catacomb-Snatch/contributors";

	public CreditsScreen(int gameWidth, int gameHeight) {
		super();
		this.gameWidth = gameWidth;
		this.gameHeight = gameHeight;

		addText(new Text(TitleMenu.CREDITS_TITLE_ID,
				MojamComponent.texts.getStatic("options.credits"), (gameWidth - 512) / 2, 50));
		addText(new Text(TitleMenu.CREDITS_TEXT_ID,
				text, (gameWidth - 512) / 2, 80));
		
		back = addButton(new Button(TitleMenu.BACK_ID, MojamComponent.texts.getStatic("back"),
				(gameWidth - 128) / 2, gameHeight - 50 - 30));
	}

	public void render(Screen screen) {

		screen.clear(0);
		screen.blit(Art.emptyBackground, 0, 0);

		super.render(screen);

		screen.blit(Art.getLocalPlayerArt()[0][6], (gameWidth - 128) / 2 - 40,
				gameHeight - 50 - 40);
	}

	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_E) {
			e.consume();
			buttons.get(0).postClick();
			//Resume on Escape
		} else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			back.postClick();
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
