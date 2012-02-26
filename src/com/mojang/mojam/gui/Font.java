package com.mojang.mojam.gui;

import java.util.HashMap;

import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Bitmap;
import com.mojang.mojam.screen.Screen;

/**
 * Font handling class
 */
public class Font {
	
	public enum FontName {
		RED, BLUE, GRAY, SM_BLACK, SM_WHITE, SM_GOLD, GOLD
	}
	
    /** List of available letters */
    private static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ    " + "0123456789-.!?/%$\\=*+,;:()&#\"'";

    /** Font glyph height/width */
    private static final int GLYPH_HEIGHT = 8;
    private static final int GLYPH_WIDTH = 8;
    
    private static final FontName DEFAULT_FONT = FontName.GOLD;
	private static final HashMap<FontName, Font> FONTS = new HashMap<FontName, Font>();
    private static Font currentFont;

	protected Bitmap[][] bitmapData;
	protected FontName fontName;

    static {
        FONTS.put(FontName.RED, new Font(Art.font_red, FontName.RED));
        FONTS.put(FontName.BLUE, new Font(Art.font_blue, FontName.BLUE));
        FONTS.put(FontName.GRAY, new Font(Art.font_gray, FontName.GRAY));
        FONTS.put(FontName.SM_BLACK, new VFont(Art.font_small_black, FontName.SM_BLACK));
        FONTS.put(FontName.SM_WHITE, new VFont(Art.font_small_white, FontName.SM_WHITE));
        FONTS.put(FontName.SM_GOLD, new VFont(Art.font_small_gold, FontName.SM_GOLD));
        FONTS.put(FontName.GOLD, new Font(Art.font_gold, FontName.GOLD));
        currentFont = getFont(DEFAULT_FONT);
    }

    protected Font(Bitmap[][] bitmapData, FontName fontName) {
        this.bitmapData = bitmapData;
        this.fontName = fontName;
    }

    /**
     * Set the font to use for all following calls
     * 
     * @param name Font name
     */
    public static void setFont(FontName fontName) {
        currentFont = getFont(fontName);
    }
    
    /**
	 * Set the font to use for all following calls to the default ones
	 */
	public static void setFontToDefault() {
		setFont(DEFAULT_FONT);
	}

    /**
     * Get the current font object
     * 
     * @return Font on success, null on error
     */
    public static Font getFont() {
        return currentFont;
    }

    public static Font getFont(FontName font) {
        Font returnFont = FONTS.get(font);
        if(returnFont == null){
            System.out.println("BAD FONT: "+currentFont);
            return FONTS.get(DEFAULT_FONT);
        }
        
        return returnFont;
    }

    /**
     * Calculate the width of the given string if drawn with the current font
     * 
     * @param text
     * @return Width (in pixels)
     */
    public static int getStringWidth(String s) {
        return getFont().getFontStringWidth(s);
    }

	/**
	 * Get the height of the current font
	 * 
	 * @return Height (in pixels)
	 */
    public static int getStringHeight() {
        return getFont().getFontStringHeight();
    }

    public int getFontStringWidth(String s) {
        return s.length() * GLYPH_WIDTH;
    }

    public int getFontStringHeight() {
        return GLYPH_HEIGHT;
    }

	/**
	 * Check if the given character is drawable
	 * 
	 * @param c Character
	 * @return True if drawable, false if not
	 */
	public static boolean isDrawableCharacter(char c) {
		return LETTERS.indexOf(Character.toUpperCase(c)) >= 0;		
	}

    /**
     * Draw the given text onto the given screen at the given position.
     * Will never create multiple lines, even if the message is too long.
     * 
     * @param screen Screen
     * @param msg Message
     * @param x X coordinate
     * @param y Y coordinate
     */
    public static void draw(Screen screen, String msg, int x, int y) {
        getFont().drawFont(screen, msg, x, y);
    }
	
    /**
     * Draw the given text onto the given screen at the given position.
     * If the length exceeds width, the text will be drawn in multiple lines.
     * 
     * @param screen Screen
     * @param msg Message
     * @param x X coordinate
     * @param y Y coordinate
     * @param width Maximum line width
     */
    public static void drawMulti(Screen screen, String msg, int x, int y, int width) {
        getFont().drawFontMulti(screen, msg, x, y, width);
    }

    /**
     * Draw the given text onto the given screen, centered
     * Will never create multiple lines, even if the message is too long.
     * 
     * @param screen 
     * @param msg 
     * @param x 
     * @param y 
     */
    public static void drawCentered(Screen screen, String msg, int x, int y) {
        int width = getStringWidth(msg);
        draw(screen, msg, x - width / 2, y - 4);
    }

    public void drawFont(Screen screen, String msg, int x, int y) {
        drawFontMulti(screen, msg, x, y, 99999);
    }

	/**
	 * Draw the given text onto the given screen at the given position.
	 * If the length exceeds width, the text will be drawn in multiple lines.
	 * 
	 * @param screen Screen
	 * @param msg Message
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param width Maximum line width
	 */
    public void drawFontMulti(Screen screen, String msg, int x, int y, int width) {
    	int startX = x;
		int fontSize = 10;
		msg = msg.toUpperCase();
		int length = msg.length();
		for (int i = 0; i < length; i++) {
			int charPosition = LETTERS.indexOf(msg.charAt(i));

			if (charPosition >= 0) {
				screen.blit(getFont().bitmapData[charPosition % 30][charPosition / 30], x, y);
				x += 8;
			} else {
				char c = msg.charAt(i);
				Bitmap characterBitmap = FontFactory.getFontCharacter(c, fontSize);
				double heightOffset = FontFactory.getHeightOffset(c, fontSize);
				screen.blit(characterBitmap, x+1, y+heightOffset);
				x += characterBitmap.w+2;
			}

			if(x > width){
				x = startX;
				y += fontSize;
			}
		}
    }
}
