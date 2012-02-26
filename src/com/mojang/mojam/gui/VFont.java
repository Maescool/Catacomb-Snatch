package com.mojang.mojam.gui;

import com.mojang.mojam.screen.Bitmap;
import com.mojang.mojam.screen.Screen;

public class VFont extends Font {
    public static String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ    " + "abcdefghijklmnopqrstuvwxyz    " + "0123456789-.!?/%$\\=*+,;:()&#\"'";
    private static final int pxFontHeight = 6;
    private static final int SPACE = 1;
    
    protected VFont(Bitmap[][] bitmapData) {
        super(bitmapData);
    }

    @Override
    public int getFontStringWidth(String s) {
        int w = 0;
        int length = s.length();
        for (int i = 0; i < length; i++) {
            int c = letters.indexOf(s.charAt(i));
            if (c < 0)
                continue;
            Bitmap image = getFont().bitmapData[c % 30][c / 30];
            w += image.w + SPACE;
        }
        return w;
    }

    @Override
    public int getFontStringHeight() {
        return pxFontHeight;
    }
    
    @Override
    public void drawFontMulti(Screen screen, String msg, int x, int y, int width) {
        int startX = x;
        int length = msg.length();
        for (int i = 0; i < length; i++) {
            int c = letters.indexOf(msg.charAt(i));
            if (c < 0)
                continue;
            Bitmap image = bitmapData[c % 30][c / 30]; 
            screen.blit(image, x, y);
            x += image.w + SPACE;
            if(x > width){
                x = startX;
                y += 10;
            }
        }
    }
}
