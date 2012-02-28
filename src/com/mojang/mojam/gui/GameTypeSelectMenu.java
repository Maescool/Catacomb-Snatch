package com.mojang.mojam.gui;

import java.awt.event.KeyEvent;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.Options;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Screen;

public class GameTypeSelectMenu extends GuiMenu {
	private int selectedItem = 0;
	private final int gameWidth;
	
	private boolean creative;
	
	public Button ExitButton;

	public GameTypeSelectMenu(int gameWidth, int gameHeight) {
		super();
		this.gameWidth = gameWidth;
		
		ClickableComponent campaignModeButton = addButton(new Button(TitleMenu.CAMPAIGN_SELECT_SCREEN, MojamComponent.texts.getStatic("gamemodemenu.campaign"),
				(gameWidth - 128) / 2, 170)); 
		ClickableComponent arcadeModeButton = addButton(new Button(TitleMenu.SELECT_LEVEL_ID, MojamComponent.texts.getStatic("gamemodemenu.arcade"),
				(gameWidth - 128) / 2, 200));
		ClickableComponent creativeModeButton = addButton(new Button(TitleMenu.SELECT_LEVEL_ID, MojamComponent.texts.getStatic("gamemodemenu.creative"),
				(gameWidth - 128) / 2, 230));
		ExitButton = (Button) addButton(new Button(TitleMenu.RETURN_TO_TITLESCREEN, MojamComponent.texts.getStatic("back"),
				(gameWidth - 128) / 2, 260));
		
		arcadeModeButton.addListener(new ButtonListener() {
			@Override
			public void buttonPressed(ClickableComponent button) {
				creative = false;
				Options.set(Options.CREATIVE, creative);
			}
		});
		
		creativeModeButton.addListener(new ButtonListener() {
			@Override
			public void buttonPressed(ClickableComponent button) {
				creative = true;
				Options.set(Options.CREATIVE, creative);
			}
		});
	}

	public void render(Screen screen) {
		screen.blit(Art.titleScreen, 0, 0);
		super.render(screen);

		Font.drawCentered(screen, MojamComponent.texts.getStatic("gamemodemenu.title"), gameWidth/2, 155);
		
		screen.blit(Art.getLocalPlayerArt()[0][6], (gameWidth - 128) / 2 - 40,
				160 + selectedItem * 30);
	}

	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
			selectedItem--;
			if (selectedItem < 0) {
				selectedItem = buttons.size() -1;
			}
		} else if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
			selectedItem++;
			if (selectedItem > buttons.size() - 1) {
				selectedItem = 0;
			}
		} else if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_E) {
			e.consume();
			buttons.get(selectedItem).postClick();
			//Resume on Escape
		} else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			ExitButton.postClick();
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
