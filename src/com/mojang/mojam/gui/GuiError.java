package com.mojang.mojam.gui;

import java.awt.event.KeyEvent;

import com.mojang.mojam.screen.Screen;

public class GuiError extends GuiMenu {

<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
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
=======
=======
>>>>>>> parent of cd61150... Cleanups, JavaDoc updates and some minor refactoring
=======
>>>>>>> parent of cd61150... Cleanups, JavaDoc updates and some minor refactoring
=======
>>>>>>> parent of cd61150... Cleanups, JavaDoc updates and some minor refactoring
	String message;
	
	public GuiError(String message){
		this.message = message;
		addButton(new Button(TitleMenu.RETURN_TO_TITLESCREEN, "Main Menu", 125, 300));
>>>>>>> parent of cd61150... Cleanups, JavaDoc updates and some minor refactoring
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
<<<<<<< HEAD
=======
	
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

<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
>>>>>>> parent of cd61150... Cleanups, JavaDoc updates and some minor refactoring
=======
>>>>>>> parent of cd61150... Cleanups, JavaDoc updates and some minor refactoring
=======
>>>>>>> parent of cd61150... Cleanups, JavaDoc updates and some minor refactoring
=======
>>>>>>> parent of cd61150... Cleanups, JavaDoc updates and some minor refactoring
}
