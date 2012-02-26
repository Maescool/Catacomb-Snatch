package com.mojang.mojam.gui;

import java.awt.event.KeyEvent;

import com.mojang.mojam.screen.Screen;

/**
 * Generic error message
 */
public class GuiError extends GuiMenu {

	private String message;

	/**
	 * Constructor
	 * 
	 * @param message
	 *            Error message
	 */
	public GuiError(String message) {
		this.message = message;

		addButton(new Button(TitleMenu.RETURN_TO_TITLESCREEN, "Main Menu", 125,
				300));
	}

	@Override
	public void render(Screen screen) {
		screen.clear(0);
		Font.setFont("red");
		Font.draw(screen, "ERROR", 15, 30);
		Font.setFont("");
		Font.drawMulti(screen, message, 20, 40, 300);
		super.render(screen);
	}
}
