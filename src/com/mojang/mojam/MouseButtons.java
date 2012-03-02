package com.mojang.mojam;

import java.awt.Point;

public class MouseButtons {

	public boolean mouseHidden = false;
	
	public boolean[] currentState = new boolean[4];
	public boolean[] nextState = new boolean[4];

	private int x;
	private int y;

	public void setNextState(int button, boolean value) {
		if (button > 3) return;
		nextState[button] = value;
	}

	public boolean isDown(int button) {
		return currentState[button];
	}

	public boolean isPressed(int button) {
		return !currentState[button] && nextState[button];
	}

	public boolean isRelased(int button) {
		return currentState[button] && !nextState[button];
	}

	public void tick() {
		for (int i = 0; i < currentState.length; i++) {
			currentState[i] = nextState[i];
		}
	}

	public void releaseAll() {
		for (int i = 0; i < nextState.length; i++) {
			nextState[i] = false;
		}
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public void setPosition(Point mousePosition) {
		if (mousePosition != null) {
			x = mousePosition.x;
			y = mousePosition.y;
		}
	}

}
