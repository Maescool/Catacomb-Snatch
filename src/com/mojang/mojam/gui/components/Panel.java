package com.mojang.mojam.gui.components;

import com.mojang.mojam.MouseButtons;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.AbstractBitmap;
import com.mojang.mojam.screen.AbstractScreen;

/**
 * 
 * Background panel
 * 
 */
public class Panel extends ClickableComponent {

	public static final int BUTTON_WIDTH = 128;
	public static final int BUTTON_HEIGHT = 24;
	private String label;
	private AbstractBitmap mainBitmap = null;
	private AbstractBitmap trCorner = null;
	private AbstractBitmap dlCorner = null;
	private AbstractBitmap drCorner = null;
	private AbstractBitmap tBand = null;
	private AbstractBitmap lBand = null;
	private AbstractBitmap rBand = null;
	private AbstractBitmap dBand = null;
	private int centerColor;

	public Panel(int x, int y, int w, int h) {
		super(x, y, w, h);
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	protected void clicked(MouseButtons mouseButtons) {
		// do nothing, handled by button listeners
	}

	@Override
	public void render(AbstractScreen screen) {

		// Cut panel textures
		if (mainBitmap != Art.button[0][0]) {
			mainBitmap = Art.button[0][0];
			trCorner = screen.createBitmap(5, 5);
			trCorner.blit(mainBitmap, -BUTTON_WIDTH + 5, 0);
			dlCorner = screen.createBitmap(5, 5);
			dlCorner.blit(mainBitmap, 0, -BUTTON_HEIGHT + 5);
			drCorner = screen.createBitmap(5, 5);
			drCorner.blit(mainBitmap, -BUTTON_WIDTH + 5, -BUTTON_HEIGHT + 5);

			int bandWidth = Math.min(getWidth(), mainBitmap.getWidth()) - 10;
			tBand = screen.createBitmap(bandWidth, 5);
			tBand.blit(mainBitmap, -5, 0);
			dBand = screen.createBitmap(bandWidth, 5);
			dBand.blit(mainBitmap, -5, -BUTTON_HEIGHT + 5);

			int bandHeight = Math.min(getHeight(), mainBitmap.getHeight()) - 10;
			lBand = screen.createBitmap(5, bandHeight);
			lBand.blit(mainBitmap, 0, -5);
			rBand = screen.createBitmap(5, bandHeight);
			rBand.blit(mainBitmap, -BUTTON_WIDTH + 5, -5);

			centerColor = mainBitmap.getPixel(BUTTON_HEIGHT * 10 + BUTTON_WIDTH);
		}

		// ==========
		// Draw panel
		// ==========

		// Center
		screen.fill(getX() + 5, getY() + 5, getWidth() - 10, getHeight() - 10, centerColor);

		// Corners
		screen.blit(mainBitmap, getX(), getY(), 5, 5);
		screen.blit(trCorner, getX() + getWidth() - 5, getY());
		screen.blit(dlCorner, getX(), getY() + getHeight() - 5);
		screen.blit(drCorner, getX() + getWidth() - 5, getY() + getHeight() - 5);

		// Sides
		int xLimit = getX() + getWidth() - 5 - tBand.getWidth();
		for (int x = getX() + 5; x != xLimit + tBand.getWidth(); x += tBand.getWidth()) {
			x = (x > xLimit) ? xLimit : x;
			screen.blit(tBand, x, getY());
			screen.blit(dBand, x, getY() + getHeight() - 5);
		}
		int yLimit = getY() + getHeight() - 5 - lBand.getHeight();
		for (int y = getY() + 5; y != yLimit + lBand.getHeight(); y += lBand.getHeight()) {
			y = (y > yLimit) ? yLimit : y;
			screen.blit(lBand, getX(), y);
			screen.blit(rBand, getX() + getWidth() - 5, y);
		}

	}
}
