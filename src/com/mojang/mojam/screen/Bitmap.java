package com.mojang.mojam.screen;

import java.util.*;

public class Bitmap {
	public int w, h;
	public int[] pixels;

	public Bitmap(int w, int h) {
		this.w = w;
		this.h = h;
		pixels = new int[w * h];
	}

	public void clear(int color) {
		Arrays.fill(pixels, color);
	}

	public void blit(Bitmap bitmap, int x, int y) {
	
	    Rect blitArea = new Rect(x, y, bitmap.w, bitmap.h);
		adjustBlitArea(blitArea);	
		
		int blitWidth = blitArea.bottomRightX - blitArea.topLeftX;

		for (int yy = blitArea.topLeftY; yy < blitArea.bottomRightY; yy++) {
			int tp = yy * w + blitArea.topLeftX;
			int sp = (yy - y) * bitmap.w + (blitArea.topLeftX - x);
			tp -= sp;
			for (int xx = sp; xx < sp + blitWidth; xx++) {
				int col = bitmap.pixels[xx];
				if (col < 0)
					pixels[tp + xx] = col;
			}
		}
	}

	public void blit(Bitmap bitmap, int x, int y, int width, int height) {
		
	    Rect blitArea = new Rect(x, y, width, height);
        adjustBlitArea(blitArea);
        		
        int blitWidth = blitArea.bottomRightX - blitArea.topLeftX;
        
        for (int yy = blitArea.topLeftY; yy < blitArea.bottomRightY; yy++) {
            int tp = yy * w + blitArea.topLeftX;
            int sp = (yy - y) * bitmap.w + (blitArea.topLeftX - x);
            tp -= sp;
            for (int xx = sp; xx < sp + blitWidth; xx++) {
                int col = bitmap.pixels[xx];
                if (col < 0)
                    pixels[tp + xx] = col;
            }
        }
	}

	public void colorBlit(Bitmap bitmap, int x, int y, int color) {
	    
	    Rect blitArea = new Rect(x, y, bitmap.w, bitmap.h);
        adjustBlitArea(blitArea);
                
        int blitWidth = blitArea.bottomRightX - blitArea.topLeftX;

		int a2 = (color >> 24) & 0xff;
		int a1 = 256 - a2;

		int rr = color & 0xff0000;
		int gg = color & 0xff00;
		int bb = color & 0xff;

		for (int yy = blitArea.topLeftY; yy < blitArea.bottomRightY; yy++) {
			int tp = yy * w + blitArea.topLeftX;
			int sp = (yy - y) * bitmap.w + (blitArea.topLeftX - x);
			for (int xx = 0; xx < blitWidth; xx++) {
				int col = bitmap.pixels[sp + xx];
				if (col < 0) {
					int r = (col & 0xff0000);
					int g = (col & 0xff00);
					int b = (col & 0xff);

					r = ((r * a1 + rr * a2) >> 8) & 0xff0000;
					g = ((g * a1 + gg * a2) >> 8) & 0xff00;
					b = ((b * a1 + bb * a2) >> 8) & 0xff;
					pixels[tp + xx] = 0xff000000 | r | g | b;
				}
			}
		}
	}

	public void fill(int x, int y, int width, int height, int color) {
	    
	    Rect blitArea = new Rect(x, y, width, height);
        adjustBlitArea(blitArea);
                
        int blitWidth = blitArea.bottomRightX - blitArea.topLeftX;

		for (int yy = blitArea.topLeftY; yy < blitArea.bottomRightY; yy++) {
			int tp = yy * w + blitArea.topLeftX;
			for (int xx = 0; xx < blitWidth; xx++) {
				pixels[tp + xx] = color;
			}
		}
	}
	
	private void adjustBlitArea(Rect blitArea){
	    
	    if (blitArea.topLeftX < 0) blitArea.topLeftX = 0;
        if (blitArea.topLeftY < 0) blitArea.topLeftY = 0;
        if (blitArea.bottomRightX > w) blitArea.bottomRightX = w;
        if (blitArea.bottomRightY > h) blitArea.bottomRightY = h;
	}
}