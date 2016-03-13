package com.mojang.mojam.gui;

import java.awt.event.KeyEvent;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.MouseButtons;
import com.mojang.mojam.gui.components.Button;
import com.mojang.mojam.gui.components.ClickableComponent;
import com.mojang.mojam.gui.components.Font;
import com.mojang.mojam.gui.components.TextInput;
import com.mojang.mojam.level.LevelList;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.AbstractScreen;

public class JoinGameMenu extends GuiMenu {	
	private TextInput ipInput;
	
	private Button joinButton;
	private Button cancelButton;

	public JoinGameMenu() {
		super();
		//load the levels!
		LevelList.createLevelList();
		joinButton = (Button) addButton(new Button(TitleMenu.PERFORM_JOIN_ID, MojamComponent.texts.getStatic("mp.join"), 100, 180));
		cancelButton = (Button) addButton(new Button(TitleMenu.CANCEL_JOIN_ID, MojamComponent.texts.getStatic("cancel"), 250, 180));
		
		ipInput = new TextInput(TitleMenu.ip, 100,120,278);
		ipInput.setFixed(true);
	}

	@Override
	public void render(AbstractScreen screen) {		
		screen.clear(0);
		screen.blit(Art.emptyBackground, 0, 0);
		Font.defaultFont().draw(screen, MojamComponent.texts.getStatic("mp.enterIP"), 100, 100);
		ipInput.render(screen);

		super.render(screen);
	}

	@Override
	public void tick(MouseButtons mouseButtons) {
		super.tick(mouseButtons);
		TitleMenu.ip = ipInput.getContent();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// Start on Enter, Cancel on Escape
		if ((e.getKeyChar() == KeyEvent.VK_ENTER)) {
			if (ipInput.getContent().length() > 0) {
				joinButton.postClick();
			}
		} else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			cancelButton.postClick();	
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
	}

	@Override
	public void keyTyped(KeyEvent e) {
		ipInput.keyTyped(e);
	}

	@Override
	public void buttonPressed(ClickableComponent button) {
	}

}
