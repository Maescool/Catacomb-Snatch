package com.mojang.mojam.gui;

import java.util.HashMap;

import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Bitmap;
import com.mojang.mojam.screen.Screen;

public class Font {
	public static String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ_   " + "0123456789-.!?/%$\\=*+,;:()&#\"'";
	private static final int pxFontHeight = 8;
	private static final int pxFontWidth = 8;
	public static HashMap<String, Font> fonts = new HashMap<String, Font>();
	private static String currentFont = "";

	static {
		fonts.put("def", new Font(Art.font_default));
		fonts.put("red", new Font(Art.font_red));
		fonts.put("blue", new Font(Art.font_blue));
		fonts.put("gray", new Font(Art.font_gray));
		fonts.put("", new Font(Art.font_gold));
	}
	
	public static void addFont(String s){
		s = s.toLowerCase();
		String s1 = s.substring(s.indexOf("font_")+5);
		int mid= s1.lastIndexOf(".");
		String fontName = s1.substring(0, mid); 
		fonts.put(fontName, new Font(Art.cut(s, 8, 8)));
		System.out.println("ADDED FONT:"+fontName);
	}
	
	public static void setFont(String s){
		currentFont = s;
	}
	
	public static Font getFont(){
		Font returnFont = fonts.get(currentFont);
		if(returnFont == null){
			System.out.println("BAD FONT: "+currentFont);
			return fonts.get("");
		}
		return returnFont;
	}
	
	public static int getStringWidth(String s) {
		return s.length() * pxFontWidth;
	}

	public static int getStringHeight() {
		return pxFontHeight;
	}

	public Bitmap[][] bitmapData;
	private Font(Bitmap[][] bitmapData) {
		this.bitmapData = bitmapData;
	}

	public static void draw(Screen screen, String msg, int x, int y) {
		drawMulti(screen, msg, x, y, 99999);
	}
	public static void drawMulti(Screen screen, String msg, int x, int y, int width) {
		int startX = x;
		msg = msg.toUpperCase();
		int length = msg.length();
		for (int i = 0; i < length; i++) {
			int c = letters.indexOf(msg.charAt(i));
			if (c < 0)
				continue;
			screen.blit(getFont().bitmapData[c % 30][c / 30], x, y);
			x += 8;
			if(x > width){
				x = startX;
				y += 10;
			}
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
