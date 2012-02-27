package com.mojang.mojam.gui;

import java.awt.Color;

import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Bitmap;
import com.mojang.mojam.screen.Screen;

public class Font {
	
	public static final Font FONT_BLUE, FONT_GOLD, FONT_GRAY, FONT_RED, FONT_BLACK_SMALL, FONT_GOLD_SMALL, FONT_WHITE_SMALL;
	
	private static Font defaultFont;
	
	/**
	 * Build predefined fonts
	 */
    static {
        String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ    " + "0123456789-.!?/%$\\=*+,;:()&#\"'";
        int glyphHeight = 8;
        int spacing = 0;
        java.awt.Font systemFont = new java.awt.Font("SansSerif", java.awt.Font.BOLD, 10);
        Color shadowColor = Color.BLACK;
        
        Color[] blueGradient = {
        	new Color(0x91aaf1),
        	new Color(0x9a9ef2),
        	new Color(0xd7d6fa),
        	new Color(0xffffff),
        	new Color(0xafadf5),
        	new Color(0x5c66ea),
        	new Color(0x8ab5f0)
        };
        FONT_BLUE = new Font(Art.font_blue, letters, glyphHeight,spacing, systemFont, blueGradient, shadowColor);
        
    	Color[] goldGradient = {
    		new Color(241, 216, 145),
    		new Color(242, 236, 153),
    		new Color(250, 250, 214),
    		new Color(255, 255, 255),
    		new Color(250, 250, 214),
    		new Color(234, 221, 91),
    		new Color(240, 195, 137)};
		FONT_GOLD = new Font(Art.font_gold, letters, glyphHeight, spacing, systemFont, goldGradient, shadowColor);
		
		Color[] grayGradient = {
	        	new Color(0xb2b2b2),
	        	new Color(0xb7b7b7),
	        	new Color(0xd6d6d6),
	        	new Color(0xececec),
	        	new Color(0xc1c1c1),
	        	new Color(0x969696),
	        	new Color(0xaeaeae)
	        };
		FONT_GRAY = new Font(Art.font_gray, letters, glyphHeight, spacing, systemFont, grayGradient, shadowColor);
		
		Color[] redGradient = {
	        	new Color(0xff657b),
	        	new Color(0xff796f),
	        	new Color(0xffbbad),
	        	new Color(0xececec),
	        	new Color(0xff9583),
	        	new Color(0xff372d),
	        	new Color(0xff5d8f)
	        };
		FONT_RED  = new Font(Art.font_red, letters, glyphHeight, spacing, systemFont, redGradient, shadowColor);
 
		letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ    " + "abcdefghijklmnopqrstuvwxyz    " + "0123456789-.!?/%$\\=*+,;:()&#\"'";
        glyphHeight = 6;
        spacing = 1;
        systemFont = new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 7);
        shadowColor = new Color(0, 0, 0, 0);
	    
        Color[] smallBlackGradient = {new Color(0x000000)};
        FONT_BLACK_SMALL = new Font(Art.font_small_black, letters, glyphHeight, spacing, systemFont, smallBlackGradient, null);
        
        Color[] smallGoldGradient = {
        		new Color(0xf0c389),
        		new Color(0xf0d289),
        		new Color(0xfbf5de),
        		new Color(0xfbf3df),
        		new Color(0xf1d891)};
        FONT_GOLD_SMALL  = new Font(Art.font_small_gold,  letters, glyphHeight, spacing, systemFont, smallGoldGradient, null);
        
        Color[] smallWhiteGradient = {new Color(0xffffff)};
        FONT_WHITE_SMALL = new Font(Art.font_small_white, letters, glyphHeight, spacing, systemFont, smallWhiteGradient, null);

	    setDefaultFont(FONT_GOLD_SMALL);
	}
    
	/**
     * Get the default font
     */
	public static Font defaultFont() {
		return defaultFont;
	}

	/**
     * Set the default font
     * 
     * @param name Font name
     */
	public static void setDefaultFont(Font defaultFont) {
		Font.defaultFont = defaultFont;
	}
    
	
	
    private Bitmap[][] bitmapData;
    private String letters;
    private int glyphHeight;
    private int letterSpacing;
    
    protected FontCharacterFactory fontCharacterFactory;

    protected Font(Bitmap[][] bitmapData, String letters, int glyphHeight,
    		int letterSpacing, java.awt.Font systemFont, Color[] gradient, Color shadowColor) {
        this.bitmapData = bitmapData;
        this.letters = letters;
        this.glyphHeight = glyphHeight;
        this.letterSpacing = letterSpacing;
        this.fontCharacterFactory = new FontCharacterFactory(systemFont, gradient, shadowColor);
    }
    
    /**
     * The width of the given string if drawn in this font
     * 
     * @param text
     */
    public int calculateStringWidth(String text) {
        int w = 0;
        for (int i = 0; i < text.length(); i++) {
        	char character = text.charAt(i);
            Bitmap image = getCharacterBitmap(character);
            w += image.w + letterSpacing;
        }
        w -= letterSpacing;
        return w;
    }
    
	/**
	 * Get the height of the current font
	 * 
	 * @return Height (in pixels)
	 */
    public int getFontHeight() {
        return glyphHeight;
    }
    
	/**
	 * Draw the given text onto the given screen at the given position
	 * 
	 * @param screen Screen
	 * @param msg Message
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param width Maximum line width in pixels
	 */
    public void draw(Screen screen, String msg, int x, int y, int width) {
    	int startX = x;
		int length = msg.length();
		for (int i = 0; i < length; i++) {
			char character = msg.charAt(i);
			Bitmap bitmap = getCharacterBitmap(character);
			int heightOffset = 0;
			if (letters.indexOf(character) < 0) {
				heightOffset = fontCharacterFactory.getHeightOffset(character);
			}
			screen.blit(bitmap, x, y+heightOffset);
			x += bitmap.w + letterSpacing;
			if(x > width - bitmap.w){
				x = startX;
				y += glyphHeight + 2;
			}
		}
    }
    
    private Bitmap getCharacterBitmap(char character) {
    	int charPosition = letters.indexOf(character);
    	if (charPosition >= 0) {
    		return bitmapData[charPosition % 30][charPosition / 30];
    	} else {
    		return fontCharacterFactory.getFontCharacter(character);
    	}
    }
    
	/**
	 * Draw the given text onto the given screen at the given position
	 * 
	 * @param screen Screen
	 * @param msg Message
	 * @param x X coordinate
	 * @param y Y coordinate
	 */
    public void draw(Screen screen, String msg, int x, int y) {
    	draw(screen, msg, x, y, Integer.MAX_VALUE);
    }
    
    /**
     * Draw the given text onto the given screen, centered.
     * 
     * Will never be split into several lines of text
     * 
     * @param screen 
     * @param msg 
     * @param x 
     * @param y 
     */
    public void drawCentered(Screen screen, String msg, int x, int y) {
        int width = calculateStringWidth(msg);
        draw(screen, msg, x - width / 2, y - 4);
    }
}
