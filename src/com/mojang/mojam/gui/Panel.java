package com.mojang.mojam.gui;

import com.mojang.mojam.MouseButtons;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Bitmap;
import com.mojang.mojam.screen.Screen;

/**
 * 
 * Background panel
 * 
 */
public class Panel extends ClickableComponent {

    public static final int BUTTON_WIDTH = 128;
    public static final int BUTTON_HEIGHT = 24;
    
	private String label;

    private Bitmap mainBitmap = null;
    private Bitmap trCorner = null;
    private Bitmap dlCorner = null;
    private Bitmap drCorner = null;
    private Bitmap tBand = null;
    private Bitmap lBand = null;
    private Bitmap rBand = null;
    private Bitmap dBand = null;
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
	public void render(Screen screen) {

	    // Cut panel textures
	    if (mainBitmap != Art.button[0][0]) {
	        mainBitmap = Art.button[0][0];
	        trCorner = new Bitmap(5, 5);
	        trCorner.blit(mainBitmap, - BUTTON_WIDTH + 5, 0);
	        dlCorner = new Bitmap(5, 5);
	        dlCorner.blit(mainBitmap, 0, - BUTTON_HEIGHT + 5);
	        drCorner = new Bitmap(5, 5);
	        drCorner.blit(mainBitmap, - BUTTON_WIDTH + 5, - BUTTON_HEIGHT + 5);
	        
	        int bandWidth = Math.min(getWidth(), mainBitmap.w) - 10;
	        tBand = new Bitmap(bandWidth, 5);
	        tBand.blit(mainBitmap, -5, 0);
	        dBand = new Bitmap(bandWidth, 5);
		    dBand.blit(mainBitmap, -5, - BUTTON_HEIGHT + 5);
	        
	        int bandHeight = Math.min(getHeight(), mainBitmap.h) - 10;
	        lBand = new Bitmap(5, bandHeight);
	        lBand.blit(mainBitmap, 0, -5);
	        rBand = new Bitmap(5, bandHeight);
	        rBand.blit(mainBitmap, - BUTTON_WIDTH + 5, -5);
	        
	        centerColor = mainBitmap.pixels[BUTTON_HEIGHT * 10 + BUTTON_WIDTH];
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
		int xLimit = getX() + getWidth() - 5 - tBand.w;
		for (int x = getX() + 5; x != xLimit + tBand.w; x += tBand.w) {
			x = (x > xLimit) ? xLimit : x;
			screen.blit(tBand, x, getY());
			screen.blit(dBand, x, getY() + getHeight() - 5);
		}
		int yLimit = getY() + getHeight() - 5 - lBand.h;
		for (int y = getY() + 5; y != yLimit + lBand.h; y += lBand.h) {
			y = (y > yLimit) ? yLimit : y;
			screen.blit(lBand, getX(), y);
			screen.blit(rBand, getX() + getWidth() - 5, y);
		}
		
	}
	
}
