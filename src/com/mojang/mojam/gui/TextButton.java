package com.mojang.mojam.gui;

import com.mojang.mojam.screen.Screen;

/**
 * Represents a button that can display any text. The two images from the bottom right corner of the
 * buttons image are used as base and the text is drawn centered using the default font.
 * 
 * @author Green Lightning
 */
public class TextButton extends Button {

	private static final int EMPTY_BUTTON_ID = 7;

	private String text;

	public TextButton(int id, String text, int x, int y) {
		super(id, EMPTY_BUTTON_ID, x, y);
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void render(Screen screen) {
		super.render(screen);
		Font.draw(screen, text, x + (w - Font.getStringWidth(text)) / 2, y + (h - 8) / 2);
	}

}
