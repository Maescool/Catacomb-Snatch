package com.mojang.mojam.gui;

import com.mojang.mojam.screen.*;

public class Font {
	public static String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ   " + "0123456789-.!?/%$\\=*+,;:()&#\"'";
	private static final int pxFontHeight = 8;
	private static final int pxFontWidth = 8;

	public static int getStringWidth(String s) {
		return s.length() * pxFontWidth;
	}

	public static int getStringHeight() {
		return pxFontHeight;
	}

	private Font() {
	}

	public static void draw(Screen screen, String msg, int x, int y) {
		msg = msg.toUpperCase();
		int length = msg.length();
		for (int i = 0; i < length; i++) {
			int c = letters.indexOf(msg.charAt(i));
			if (c < 0)
				continue;
			screen.blit(Art.font[c % 29][c / 29], x, y);
			x += 8;
		}
	}

	/**
	 * draws the text centered
	 */
	public static void drawCentered(Screen screen, String msg, int x, int y) {
		int width = getStringWidth(msg);
		draw(screen, msg, x - width / 2, y - 4);
	}
}
