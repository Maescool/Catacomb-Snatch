package com.mojang.mojam.gui;

import java.util.ArrayList;
import java.util.List;

import com.mojang.mojam.MouseButtons;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Screen;

public class Button extends GuiComponent {

	private List<ButtonListener> listeners;

	private boolean isPressed;

	protected int x;
	protected int y;
	protected final int w;
	protected final int h;

	private final int id;

	private boolean isImageButton;
	private String label;
	private int ix;
	private int iy;
	private boolean performClick = false;

	public Button(int id, int buttonImageIndex, int x, int y) {		
		this.id = id;
		this.x = x;
		this.y = y;
		this.w = 128;
		this.h = 24;
		this.ix = buttonImageIndex % 2;
		this.iy = buttonImageIndex / 2;
		isImageButton = true;
	}
	
	public Button(int id, String label, int x, int y) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.w = Font.getStringWidth(label);
		this.h = Font.getStringHeight();
		this.label = label;
		isImageButton = false;
	}

	@Override
	public void tick(MouseButtons mouseButtons) {
		super.tick(mouseButtons);

		int mx = mouseButtons.getX() / 2;
		int my = mouseButtons.getY() / 2;
		isPressed = false;
		if (mx >= x && my >= y && mx < (x + w) && my < (y + h)) {
			if (mouseButtons.isRelased(1)) {
				postClick();
			} else if (mouseButtons.isDown(1)) {
				isPressed = true;
			}
		}

		if (performClick) {
			if (listeners != null) {
				for (ButtonListener listener : listeners) {
					listener.buttonPressed(this);
				}
			}
			performClick = false;
		}
	}

	public void postClick() {
		performClick = true;
	}

	@Override
	public void render(Screen screen) {

		if (isPressed) {
			if(isImageButton)
				screen.blit(Art.buttons[ix][iy * 2 + 1], x, y);
			else
				Font.draw(screen, label, x, y);
		} else {
			if(isImageButton)
				screen.blit(Art.buttons[ix][iy * 2 + 0], x, y);
			else
				Font.draw(screen, label, x, y);			
		}
	}

	public boolean isPressed() {
		return isPressed;
	}

	public void addListener(ButtonListener listener) {
		if (listeners == null) {
			listeners = new ArrayList<ButtonListener>();
		}
		listeners.add(listener);
	}

	public int getId() {
		return id;
	}
	
	public void setXPosition(int x) {
		this.x = x;
	}
	
	public void setYPosition(int y) {
		this.y = y;
	}
}
