package com.mojang.mojam.gui.components;

import com.mojang.mojam.MouseButtons;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.AbstractBitmap;
import com.mojang.mojam.screen.AbstractScreen;
import com.mojang.mojam.screen.MojamBitmap;

public class Button extends ClickableComponent {

    public static final int BUTTON_WIDTH = 128;
    public static final int BUTTON_HEIGHT = 24;
    
	private final int id;

	private String label;

    private AbstractBitmap mainBitmap = null;
    private AbstractBitmap rightBorderBitmap = null;
    private AbstractBitmap middleBitmap = null;

	public Button(int id, String label, int x, int y) {
		super(x, y, BUTTON_WIDTH, BUTTON_HEIGHT);
		this.id = id;
		this.label = label;
	}

    public Button(int id, String label, int x, int y, int w, int h) {
        super(x, y, w, h);
        this.id = id;
        this.label = label;
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

		if(enabled){
			if (isPressed()) {
			    blitBackground(screen, 1);
			} else {
			    blitBackground(screen, 0);
			}
		} else {
			blitBackground(screen, 2);
		}
		Font.defaultFont().draw(screen, label, getX() + getWidth() / 2, getY() + getHeight() / 2, Font.Align.CENTERED);
	}
	
	private void blitBackground(AbstractScreen screen, int bitmapId) {
	    
	    // Default width button
	    if (getWidth() == BUTTON_WIDTH) {
            screen.blit(Art.button[0][bitmapId], getX(), getY());
	    }
	    
	    // Custom width buttons
	    else {
    	    // Cut button textures
    	    if (mainBitmap != Art.button[0][bitmapId]) {
    	        mainBitmap = Art.button[0][bitmapId];
    	        rightBorderBitmap = screen.createBitmap(10, BUTTON_HEIGHT);
    	        rightBorderBitmap.blit(mainBitmap, - BUTTON_WIDTH + 10, 0);
                middleBitmap = screen.createBitmap(1, BUTTON_HEIGHT);
                middleBitmap.blit(mainBitmap, -10, 0);
    	    }
    	    
    	    // Draw button
            screen.blit(mainBitmap, getX(), getY(), 10, getHeight());
            for (int x = getX() + 10; x < getX() + getWidth() - 10; x++) {
                screen.blit(middleBitmap, x, getY());
            }
            screen.blit(rightBorderBitmap, getX() + getWidth() - 10, getY());
	    }
	}

	public int getId() {
		return id;
	}
	
	private MojamBitmap getBitmap(int bitmapId) {
	    
		MojamBitmap b = new MojamBitmap(getWidth(), getHeight());
		
	    // Default width button
	    if (getWidth() == BUTTON_WIDTH) {
            b.blit(Art.button[0][bitmapId], 0, 0);
	    }
	    
	    // Custom width buttons
	    else {
    	    // Cut button textures
    	    if (mainBitmap != Art.button[0][bitmapId]) {
    	        mainBitmap = Art.button[0][bitmapId];
    	        rightBorderBitmap = new MojamBitmap(10, BUTTON_HEIGHT);
    	        rightBorderBitmap.blit(mainBitmap, - BUTTON_WIDTH + 10, 0);
                middleBitmap = new MojamBitmap(1, BUTTON_HEIGHT);
                middleBitmap.blit(mainBitmap, -10, 0);
    	    }
    	    
    	    // Draw button
            b.blit(mainBitmap, 0, 0, 10, getHeight());
            for (int x = 10; x < getWidth() - 10; x++) {
                b.blit(middleBitmap, x, 0);
            }
            b.blit(rightBorderBitmap, getWidth() - 10, 0);
	    }
	    
	    Font.defaultFont().draw(b, label, getX() + getWidth() / 2, getY() + getHeight() / 2, Font.Align.CENTERED);
	    return b;
	}

	public MojamBitmap[] getBitmaps() {
		MojamBitmap[] bmps = new MojamBitmap[3];
		bmps[0] = getBitmap(0);
		bmps[1] = getBitmap(1);
		bmps[2] = getBitmap(2);
		return bmps;
	}
}
