package com.mojang.mojam;

import java.awt.Point;

public class MouseButtons {

	public boolean mouseHidden = false;
	public boolean renderMouse = false;
	
	public boolean[] currentState = new boolean[4];
	public boolean[] nextState = new boolean[4];

	private int ox;
	private int oy;
	private int x;
	private int y;
	public int jx;
	public int jy;
	public int sx;
	public int sy;

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
			this.setPosition(mousePosition.x, mousePosition.y);
		}
	}

	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public void updatePosition(int nx, int ny, boolean mouseMoved, boolean joyMoved, boolean inLevel) {
		ox = x;
		oy = y;
		if (!mouseMoved && joyMoved) {
			if (sx != 0 && inLevel) {
				x = MojamComponent.GAME_WIDTH/2 + sx;
			} else {
				x = x + jx;
			}
			if (sy != 0 && inLevel) {
				y = (MojamComponent.GAME_HEIGHT/2 - 24) + sy;
			} else {
				y = y + jy;
			}
		} else if (mouseMoved) {
			x = nx;
			y = ny;
		}
	}

}
