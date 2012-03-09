package com.mojang.mojam.gui.components;

import java.util.ArrayList;
import java.util.List;

import com.mojang.mojam.MouseButtons;

public abstract class ClickableComponent extends VisibleComponent {

	private List<ButtonListener> listeners;

	private boolean isPressed;
	protected boolean performClick = false;
	protected boolean performHover = false;
	public boolean enabled = true;

	public ClickableComponent(int x, int y, int w, int h) {
		super(x, y, w, h);
	}

	@Override
	public void tick(MouseButtons mouseButtons) {
		super.tick(mouseButtons);

		int mx = mouseButtons.getX();
		int my = mouseButtons.getY();
		isPressed = false;
		if (enabled && mx >= getX() && my >= getY() && mx < (getX() + getWidth()) && my < (getY() + getHeight())) {
			postHover();
			if (mouseButtons.isRelased(1)) {
				postClick();
			} else if (mouseButtons.isDown(1)) {
				isPressed = true;
			}
		}

		if (performClick) {
			clicked(mouseButtons);
			if (listeners != null) {
				for (ButtonListener listener : listeners) {
					listener.buttonPressed(this);
				}
			}
			performClick = false;
		}
		if (performHover) {
			if (listeners != null) {
				for (ButtonListener listener : listeners) {
					listener.buttonHovered(this);
				}
			}
			performHover = false;
		}
	}
	
	public void postHover() {
		performHover = true;
	}

	/**
	 * Internal function, forcing the component to run its clicked() method and
	 * iterate over listeners
	 */
	public void postClick() {
		performClick = true;
	}

	/**
	 * This component is being clicked on?
	 * 
	 * @return boolean
	 */
	public boolean isPressed() {
		return isPressed;
	}

	/**
	 * Adds a listener to the internal list, to get called when this component
	 * has been clicked
	 * 
	 * @param listener
	 */
	public void addListener(ButtonListener listener) {
		if (listeners == null) {
			listeners = new ArrayList<ButtonListener>();
		}
		listeners.add(listener);
	}

	/**
	 * Triggered when clicked
	 * 
	 * @param mouseButtons
	 */
	protected abstract void clicked(MouseButtons mouseButtons);

}
