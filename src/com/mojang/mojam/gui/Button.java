package com.mojang.mojam.gui;

import com.mojang.mojam.MouseButtons;
import com.mojang.mojam.screen.*;

public class Button extends ClickableComponent {

	private final int id;

	private String label;
	private boolean performClick = false;

	public Button(int id, String label, int x, int y) {
		super(x, y, 128, 24);
		this.id = id;
		this.label = label;
	}

	@Override
	protected void clicked(MouseButtons mouseButtons) {
		// do nothing, handled by button listeners
	}

	@Override
	public void render(Screen screen) {

		if (isPressed()) {
			screen.blit(Art.button[0][1], getX(), getY());
		} else {
			screen.blit(Art.button[0][0], getX(), getY());
		}
		Font.drawCentered(screen, label, getX() + getWidth() / 2, getY() + getHeight() / 2);
	}

	public int getId() {
		return id;
	}
}
