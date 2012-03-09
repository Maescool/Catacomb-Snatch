package com.mojang.mojam.screen;

import java.util.Arrays;

public class DesktopBitmap extends Bitmap {

	public DesktopBitmap() {
		super();
	}

	public DesktopBitmap(int w, int h) {
		super(w, h);
	}

	public DesktopBitmap(int w, int h, int[] pixels) {
		super(w, h, pixels);
	}

	public DesktopBitmap(int[][] pixels2D) {
		super(pixels2D);
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
			    int alpha = (col >> 24) & 0xff;
			    
			    if (alpha == 255) 
			        pixels[tp + xx] = col;
			    else
			        pixels[tp + xx] = blendPixels(pixels[tp + xx], col);
			}
		}
	}

	public int blendPixels(int backgroundColor, int pixelToBlendColor) {

		int alpha_blend = (pixelToBlendColor >> 24) & 0xff;

		int alpha_background = 256 - alpha_blend;

		int rr = backgroundColor & 0xff0000;
		int gg = backgroundColor & 0xff00;
		int bb = backgroundColor & 0xff;

		int r = (pixelToBlendColor & 0xff0000);
		int g = (pixelToBlendColor & 0xff00);
		int b = (pixelToBlendColor & 0xff);

		r = ((r * alpha_blend + rr * alpha_background) >> 8) & 0xff0000;
		g = ((g * alpha_blend + gg * alpha_background) >> 8) & 0xff00;
		b = ((b * alpha_blend + bb * alpha_background) >> 8) & 0xff;

		return 0xff000000 | r | g | b;
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
	            int alpha = (col >> 24) & 0xff;
	            
	            if (alpha == 255) 
	                pixels[tp + xx] = col;
	            else 
	                pixels[tp + xx] = blendPixels(pixels[tp + xx], col);
	        }
	    }
	}

    public void alphaBlit(Bitmap bitmap, int x, int y, int alpha) {

        if(alpha == 255)
        {
            this.blit(bitmap, x, y);
            return;
        }
        
        Rect blitArea = new Rect(x, y, bitmap.w, bitmap.h);
        adjustBlitArea(blitArea);
                
        int blitWidth = blitArea.bottomRightX - blitArea.topLeftX;

        for (int yy = blitArea.topLeftY; yy < blitArea.bottomRightY; yy++) {
            int tp = yy * w + blitArea.topLeftX;
            int sp = (yy - y) * bitmap.w + (blitArea.topLeftX - x);
            for (int xx = 0; xx < blitWidth; xx++) {
                int col = bitmap.pixels[sp + xx];
                if (col < 0) {
                    
                    int r = (col & 0xff0000);
                    int g = (col & 0xff00);
                    int b = (col & 0xff);
                    col = (alpha << 24) | r | g | b;
                    int color = pixels[tp + xx];
                    pixels[tp + xx] = this.blendPixels(color, col);
                }
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


    /**
     * Fills semi-transparent region on screen
     * @param x position on screen
     * @param y position on screen
     * @param width of the region
     * @param height of the region
     * @param color to fill the region
     * @param alpha range from 0x00 (transparent) to 0xff (opaque)
     */
    public void alphaFill(int x, int y, int width, int height, int color, int alpha) {

        if(alpha == 255)
        {
            this.fill(x, y, width, height, color);
            return;
        }
        
        Bitmap bmp = Bitmap.getSystemCompatibleBitmap(width, height);
        bmp.fill(0, 0, width, height, color);
        
        this.alphaBlit(bmp, x, y, alpha);
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
	

	public void adjustBlitArea(Rect blitArea){
	    
	    if (blitArea.topLeftX < 0) blitArea.topLeftX = 0;
        if (blitArea.topLeftY < 0) blitArea.topLeftY = 0;
        if (blitArea.bottomRightX > w) blitArea.bottomRightX = w;
        if (blitArea.bottomRightY > h) blitArea.bottomRightY = h;
	}

	public void rectangle(int x, int y, int width, int height, int color) {
		int x0 = x;
		int x1 = x + width;
		int y0 = y;
		int y1 = y + height;
		if (x0 < 0)
			x0 = 0;
		if (y0 < 0)
			y0 = 0;
		if (x1 > w)
			x1 = w;
		if (y1 > h)
			y1 = h;

		for (int yy = y0; yy < y1; yy++) {
			setPixel(x0, yy, color);
			setPixel(x1 - 1, yy, color);
		}

		for (int xx = x0; xx < x1; xx++) {
			setPixel(xx, y0, color);
			setPixel(xx, y1 - 1, color);
		}
	}

	public void setPixel(int x, int y, int color) {
		pixels[x+y*w]=color;
		
	}

	public void circle(int centerX, int centerY, int radius, int color) {
		int d = 3 - (2 * radius);
		int x = 0;
		int y = radius;
	
		do {
			setPixel(centerX + x, centerY + y, color);
			setPixel(centerX + x, centerY - y, color);
			setPixel(centerX - x, centerY + y, color);
			setPixel(centerX - x, centerY - y, color);
			setPixel(centerX + y, centerY + x, color);
			setPixel(centerX + y, centerY - x, color);
			setPixel(centerX - y, centerY + x, color);
			setPixel(centerX - y, centerY - x, color);
			
			if (d < 0) {
				d = d + (4 * x) + 6;
			} else {
				d = d + 4 * (x - y) + 10;
				y--;
			}
			x++;
		} while (x <= y);
	}
	

	public void circleFill(int centerX, int centerY, int radius, int color) {
		int d = 3 - (2 * radius);
		int x = 0;
		int y = radius;
	
		do {
			horizonalLine(centerX + x, centerX - x, centerY + y, color);
			horizonalLine(centerX + x, centerX - x, centerY - y, color);
			horizonalLine(centerX + y, centerX - y, centerY + x, color);
			horizonalLine(centerX + y, centerX - y, centerY - x, color);
			
			if (d < 0) {
				d = d + (4 * x) + 6;
			} 
			else {
				d = d + 4 * (x - y) + 10;
				y--;
			}
			x++;
		} while (x <= y);
	}
	
	public void horizonalLine(int x1, int x2, int y, int color) {
		if (x1 > x2) {
			int xx = x1;
			x1 = x2;
			x2 = xx;
		}		
		
		for (int xx = x1; xx <= x2; xx++) {
			setPixel(xx, y, color);			
		}
	}
}