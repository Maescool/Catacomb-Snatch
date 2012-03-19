package com.mojang.mojam.gui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Stack;

import com.mojang.mojam.gui.components.ButtonListener;

public class MenuStack extends Stack<GuiMenu> implements KeyListener {

	private static final long serialVersionUID = 1L;
	private ButtonListener stackButtonListener;
	
	/***
	 * Creates an empty MenuStack
	 */
	public MenuStack() {
		super();
	}
	
	/***
	 * sets the ButtonListener that will be applied to all
	 * menus in the stack after this call.
	 * *WARNING* only use the add method when added menus
	 * to the stack, else they won't have this ButtonListener
	 * added.
	 * 
	 * @param bl the ButtonListener to be added to all menus.
	 */
	public void setStackButtonListener(ButtonListener bl) {
		stackButtonListener = bl;
	}
	
	/***
	 * adds a menu to the stack and automatically adds
	 * the stack button listener as a button listener
	 * 
	 * @return true if it succeeded, false otherwise
	 */
	public boolean add(GuiMenu menu) {
		try {
			super.add(menu);
			menu.addButtonListener(stackButtonListener);
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/***
	 * attempts to pop the top menu off the stack
	 * 
	 * @return the menu that was popped or null otherwise
	 */
	public GuiMenu safePop() {
		try {
			return pop();
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void keyPressed(KeyEvent e) {
		if(!empty())
			peek().keyPressed(e);
	}
	
	public void keyReleased(KeyEvent e) {
		if(!empty())
			peek().keyReleased(e);
	}
	
	public void keyTyped(KeyEvent e) {
		if(!empty())
			peek().keyTyped(e);
	}
}