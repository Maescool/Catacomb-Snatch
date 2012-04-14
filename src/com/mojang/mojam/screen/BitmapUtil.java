package com.mojang.mojam.screen;

import java.awt.image.BufferedImage;

/**
 * Useless utilities for the game but usefull for the Launcher
 */
public class BitmapUtil {
	
	/**
	 * Method for converting Bitmaps to BufferedImages . 
	 * @param b Bitmap to convert 
	 * @return Converted Bitmap 
	 */
	public static BufferedImage convert(AbstractBitmap b) { 
		int w = b.getWidth(); 
		int h = b.getHeight();
		
		int pixels[] = new int[b.getPixelSize()];
		
		for (int i = 0; i < pixels.length; i++) {
			pixels[i] = b.getPixel(i);
		}
		
		BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		bi.setRGB(0,  0, w, h, pixels, 0, w);
		
		return bi; 
	}
	
	/**
	 * Method for converting BufferedImages to Bitmaps . 
	 * @param bi BufferedImage to convert 
	 * @return Converted Image 
	 */
	public static MojamBitmap convert(BufferedImage bi) {
		
		int w = bi.getWidth();
		int h = bi.getHeight();
		
		MojamBitmap result = new MojamBitmap(w, h);
		bi.getRGB(0, 0, w, h, result.pixels, 0, w);
		
		return result;
	}

	/**
	 * Replaces all pixels with color oldc with color newc .
	 * @param b Bitmap
	 * @param oldc Old color
	 * @param newc New color
	 */
	public static void replace(AbstractBitmap b, int oldc, int newc) {
		for (int i = 0; i < b.getPixelSize(); i++) {
			if (b.getPixel(i) == oldc) b.setPixel(i, newc);
		}
	}
	
}
