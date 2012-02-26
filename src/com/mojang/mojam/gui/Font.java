package com.mojang.mojam.gui;

import java.util.HashMap;

import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Bitmap;
import com.mojang.mojam.screen.Screen;

/**
 * Font handler
 */
public class Font {
	/**
	 * List of available characters, in the order they appear in the bitmap file
	 */
	private static String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ    "
			+ "0123456789-.!?/%$\\=*+,;:()&#\"'";

	// Font size
	private static final int FONT_HEIGHT = 8;
	private static final int FONT_WIDTH = 8;

	// Font map
	public static HashMap<String, Font> fonts = new HashMap<String, Font>();
	private static String currentFont = "";

	// Font bitmaps
	private Bitmap[][] bitmapData;

	// Initialize font list
	static {
		fonts.put("", new Font(Art.font_default));
		fonts.put("", new Font(Art.font_red));
		fonts.put("", new Font(Art.font_blue));
		fonts.put("", new Font(Art.font_gray));
		fonts.put("", new Font(Art.font_gold));
	}

	/**
	 * Add a new font to the list
	 * 
	 * @param name
	 *            Font name
	 */
	public static void addFont(String name) {
		name = name.toLowerCase();
		String s1 = name.substring(name.indexOf("font_") + 5);
		int mid = s1.lastIndexOf(".");
		String fontName = s1.substring(0, mid);
		fonts.put(fontName, new Font(Art.cut(name, 8, 8)));
		System.out.println("ADDED FONT:" + fontName);
	}

	/**
	 * Check if the given character is printable
	 * 
	 * @param key
	 *            Key
	 * @return True if printable, false if not
	 */
	public static boolean isPrintableCharacter(char key) {
		return letters.indexOf(Character.toUpperCase(key)) >= 0;
	}

	public static void setFont(String s) {
		currentFont = s;
	}

	/**
	 * Get the current font
	 * 
	 * @return Current font
	 */
	public static Font getFont() {
		Font returnFont = fonts.get(currentFont);

		if (returnFont == null) {
			System.out.println("BAD FONT: " + currentFont);
			return fonts.get("");
		}

		return returnFont;
	}

	/**
	 * Calculate the width of the given string
	 * 
	 * @param s
	 *            String
	 * @return Width
	 */
	public static int getStringWidth(String s) {
		return s.length() * FONT_WIDTH;
	}

	/**
	 * Get the height of a string
	 * 
	 * @return String height
	 */
	public static int getStringHeight() {
		return FONT_HEIGHT;
	}

	/**
	 * Set bitmap data
	 * 
	 * @param bitmapData
	 *            Bitmap data
	 */
	private Font(Bitmap[][] bitmapData) {
		this.bitmapData = bitmapData;
	}

	/**
	 * Draw the given message onto the given screen using the current font, at
	 * position x/y, and if the given width is exceeded, multiple lines are
	 * drawn as needed
	 * 
	 * @param screen
	 *            Screen
	 * @param msg
	 *            Message
	 * @param x
	 *            X coordinate
	 * @param y
	 *            Y coordinate
	 * @param width
	 *            Line width
	 */
	public static void drawMulti(Screen screen, String msg, int x, int y,
			int width) {
		int startX = x;
		msg = msg.toUpperCase();
		int length = msg.length();
		for (int i = 0; i < length; i++) {
			int c = letters.indexOf(msg.charAt(i));
			if (c < 0)
				continue;
			screen.blit(getFont().bitmapData[c % 30][c / 30], x, y);
			x += 8;
			if (x > width) {
				x = startX;
				y += 10;
			}
		}
	}

	/**
	 * Draw the given message onto the given screen using the current font, at
	 * position x/y
	 * 
	 * @param screen
	 *            Screen
	 * @param msg
	 *            Message
	 * @param x
	 *            X coordinate
	 * @param y
	 *            Y coordinate
	 */
	public static void draw(Screen screen, String msg, int x, int y) {
		drawMulti(screen, msg, x, y, 99999);
	}

	/**
	 * Draw the given message onto the given screen using the current font,
	 * centered
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
