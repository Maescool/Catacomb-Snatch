package com.mojang.mojam.gui;

import java.awt.event.KeyEvent;

import com.mojang.mojam.gui.components.Button;
import com.mojang.mojam.gui.components.ClickableComponent;
import com.mojang.mojam.gui.components.Font;
import com.mojang.mojam.screen.AbstractScreen;

public class GuiError extends GuiMenu {

	String message;
	
	public GuiError(String message){
		this.message = message;
		addButton(new Button(TitleMenu.RETURN_TO_TITLESCREEN, "Main Menu", 125, 300));
	}
	
	@Override
	public void render(AbstractScreen screen) {
		screen.clear(0);
		Font.FONT_RED.draw(screen, "ERROR", 15, 30);
		Font.defaultFont().draw(screen, message, 20, 40, 300);
		super.render(screen);
	}
	
	@Override
	public void keyPressed(KeyEvent arg0) {
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
	}
	
	public void buttonPressed(ClickableComponent button) {
	}

}
