package com.mojang.mojam.gui;

import java.util.HashMap;

import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Bitmap;
import com.mojang.mojam.screen.Screen;

/**
 * Font handling class
 */
public class Font {

	/** List of available letters */
	public static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ    " + "0123456789-.!?/%$\\=*+,;:()&#\"'";

	/** Default font */
	private static final String DEFAULT_FONT = "gold";

	/** Font glyph height/width */
	private static final int GLYPH_HEIGHT = 8;
	private static final int GLYPH_WIDTH = 8;

	public static HashMap<String, Font> fonts = new HashMap<String, Font>();
	private static String currentFont = DEFAULT_FONT;

	private Bitmap[][] bitmapData;

	static {
		fonts.put("default", new Font(Art.font_default));
		fonts.put("red", new Font(Art.font_red));
		fonts.put("blue", new Font(Art.font_blue));
		fonts.put("gray", new Font(Art.font_gray));
		fonts.put("gold", new Font(Art.font_gold));
	}
	
	/**
	 * Add a font to the font list
	 * 
	 * @param name Font name
	 */
	public static void addFont(String name){
		name = name.toLowerCase();
		String s1 = name.substring(name.indexOf("font_")+5);
		int mid= s1.lastIndexOf(".");
		String fontName = s1.substring(0, mid); 
		fonts.put(fontName, new Font(Art.cut(name, 8, 8)));
		System.out.println("ADDED FONT:"+fontName);
	}

	/**
	 * Set the font to use for all following calls
	 * 
	 * @param name Font name
	 */
	public static void setFont(String s){
		if (fonts.containsKey(s)) {
			currentFont = s;
		}
		else {
			System.out.println("Invalid font request: " + s);
		}
	}
	
	/**
	 * Set the font to use for all following calls to the default ones
	 */
	public static void setFontToDefault() {
		currentFont = DEFAULT_FONT;
	}
	
	/**
	 * Get the current font object
	 * 
	 * @return Font on success, null on error
	 */
	public static Font getFont(){
		Font returnFont = fonts.get(currentFont);
		
		if(returnFont == null){
			System.out.println("BAD FONT: "+currentFont);
			return fonts.get("");
		}
		
		return returnFont;
	}
	
	/**
	 * Calculate the width of the given string if drawn with the current font
	 * 
	 * @param text
	 * @return Width (in pixels)
	 */
	public static int getStringWidth(String text) {
		return text.length() * GLYPH_WIDTH;
	}

	/**
	 * Get the height of the current font
	 * 
	 * @return Height (in pixels)
	 */
	public static int getStringHeight() {
		return GLYPH_HEIGHT;
	}
	
	private Font(Bitmap[][] bitmapData) {
		this.bitmapData = bitmapData;
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
				screen.blit(characterBitmap, x+1, (int)(y+heightOffset+0.5));
				x += characterBitmap.w+2;
			}
			if(x > width){
				x = startX;
				y += fontSize;
			}
		}
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
		drawMulti(screen, msg, x, y, 99999);
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
}
