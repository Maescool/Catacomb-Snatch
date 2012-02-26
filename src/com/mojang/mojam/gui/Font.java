package com.mojang.mojam.gui;

import java.util.HashMap;

import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Bitmap;
import com.mojang.mojam.screen.Screen;

public class Font {
    public static String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ    " + "0123456789-.!?/%$\\=*+,;:()&#\"'";
    private static final int pxFontHeight = 8;
    private static final int pxFontWidth = 8;
    public static HashMap<String, Font> fonts = new HashMap<String, Font>();
    private static Font currentFont;

    static {
        //fonts.put("", new Font(Art.font_default));
        fonts.put("red", new Font(Art.font_red));
        fonts.put("blue", new Font(Art.font_blue));
        fonts.put("gray", new Font(Art.font_gray));
        fonts.put("sm_black", new VFont(Art.font_small_black));
        fonts.put("", new Font(Art.font_gold));
        currentFont = getFont("");
    }

    public static void setFont(String fontName) {
        currentFont = getFont(fontName);
    }

    public static Font getFont() {
        return currentFont;
    }

    public static Font getFont(String font) {
        Font returnFont = fonts.get(font);
        if(returnFont == null){
            System.out.println("BAD FONT: "+currentFont);
            return fonts.get("");
        }
        return returnFont;
    }

    public static int getStringWidth(String s) {
        return getFont().getFontStringWidth(s);
    }

    public static int getStringHeight() {
        return getFont().getFontStringHeight();
    }

    public Bitmap[][] bitmapData;
    protected Font(Bitmap[][] bitmapData) {
        this.bitmapData = bitmapData;
    }

    public int getFontStringWidth(String s) {
        return s.length() * pxFontWidth;
    }

    public int getFontStringHeight() {
        return pxFontHeight;
    }

    public static void draw(Screen screen, String msg, int x, int y) {
        getFont().drawFont(screen, msg, x, y);
    }

    public static void drawMulti(Screen screen, String msg, int x, int y, int width) {
        getFont().drawFontMulti(screen, msg, x, y, width);
    }

    public void drawFont(Screen screen, String msg, int x, int y) {
        drawFontMulti(screen, msg, x, y, 99999);
    }

    public void drawFontMulti(Screen screen, String msg, int x, int y, int width) {
        int startX = x;
        msg = msg.toUpperCase();
        int length = msg.length();
        for (int i = 0; i < length; i++) {
            int c = letters.indexOf(msg.charAt(i));
            if (c < 0)
                continue;
            screen.blit(bitmapData[c % 30][c / 30], x, y);
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
