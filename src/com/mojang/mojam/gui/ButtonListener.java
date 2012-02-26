package com.mojang.mojam.gui;

/**
 * Button listener interface
 */
public interface ButtonListener {
	/**
	 * Called every time a button is pressed
	 * 
	 * @param button
	 *            Reference to the pressed button object
	 */
	public void buttonPressed(ClickableComponent button);
}
