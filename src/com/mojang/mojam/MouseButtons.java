package com.mojang.mojam;

import java.awt.Point;

public class MouseButtons {

	private boolean[] currentState = new boolean[4];
	private boolean[] nextState = new boolean[4];

	private int x;
	private int y;

	public void setNextState(int button, boolean value) {
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
	
	public void setX(int x) {
		this.x = x;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	public boolean[] getAllCurrentState() {
		return currentState;
	}
	
	public boolean[] getAllNextState() {
		return nextState;
	}
	
	public void setAllCurrentState(boolean[] state) {
		currentState = state;
	}
	
	public void setAllNextState(boolean[] state) {
		nextState = state;
	}
	
	public void setPosition(Point mousePosition) {
		if (mousePosition != null) {
			x = mousePosition.x;
			y = mousePosition.y;
		}
	}

}
