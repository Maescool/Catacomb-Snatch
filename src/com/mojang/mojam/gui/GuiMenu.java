package com.mojang.mojam.gui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.*;

import com.mojang.mojam.MouseButtons;
import com.mojang.mojam.screen.Screen;

/**
 * GUI menu base class
 */
public abstract class GuiMenu extends GuiComponent implements ButtonListener,
		KeyListener {

	protected List<ClickableComponent> buttons = new ArrayList<ClickableComponent>();

	/**
	 * Add a button to this menu
	 * 
	 * @param button
	 *            Button
	 * @return Button
	 */
	protected ClickableComponent addButton(ClickableComponent button) {
		buttons.add(button);
		button.addListener(this);
		return button;
	}

	/**
	 * Remove a button from this menu
	 * 
	 * @param button
	 *            Button
	 * @return Button reference on success, null on error
	 */
	protected ClickableComponent removeButton(ClickableComponent button) {
		if (buttons.remove(button)) {
			return button;
		} else {
			return null;
		}
	}

	@Override
	public void render(Screen screen) {
		super.render(screen);

		for (ClickableComponent button : buttons) {
			button.render(screen);
		}
	}

	@Override
	public void tick(MouseButtons mouseButtons) {
		super.tick(mouseButtons);

		for (ClickableComponent button : buttons) {
			button.tick(mouseButtons);
		}
	}

	public void addButtonListener(ButtonListener listener) {
		for (ClickableComponent button : buttons) {
			button.addListener(listener);
		}
	}

	@Override
	public void buttonPressed(ClickableComponent button) {
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
	}
}
