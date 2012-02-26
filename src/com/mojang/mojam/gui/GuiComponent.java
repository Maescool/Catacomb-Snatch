package com.mojang.mojam.gui;

import com.mojang.mojam.MouseButtons;
import com.mojang.mojam.screen.Screen;

/**
 * Generic GUI component class
 */
public abstract class GuiComponent {

	/**
	 * Render this component onto the given screen
	 * 
	 * @param screen
	 *            Screen
	 */
	public void render(Screen screen) {
	}

	/**
	 * Calculate the next timer tick
	 * 
	 * @param mouseButtons
	 *            Mouse button states
	 */
	public void tick(MouseButtons mouseButtons) {

	}
}
