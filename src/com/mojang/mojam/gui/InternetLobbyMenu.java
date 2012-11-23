package com.mojang.mojam.gui;

import java.awt.event.KeyEvent;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.gui.components.Button;
import com.mojang.mojam.gui.components.ClickableComponent;
import com.mojang.mojam.gui.components.Font;
import com.mojang.mojam.screen.AbstractScreen;
import com.mojang.mojam.screen.Art;

public class InternetLobbyMenu extends GuiMenu {
	
	private Button cancelButton;
	public Boolean loading = true;
	
	public InternetLobbyMenu(){
		super();
		cancelButton = (Button) addButton(new Button(TitleMenu.CANCEL_JOIN_ID, MojamComponent.texts.getStatic("cancel"), 364, 335));
	}
	
	@Override
	public void render(AbstractScreen screen) {

		screen.clear(0);
		screen.blit(Art.emptyBackground, 0, 0);
		Font font = Font.defaultFont();
		
		if (loading){
			font.draw(screen, MojamComponent.texts.getStatic("mp.waitingForClient"), 100, 100);
		}
		

		super.render(screen);
	}

	@Override
	public void buttonPressed(ClickableComponent button) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			cancelButton.postClick();
		} else {
			super.keyPressed(e);
		}
	}

}
