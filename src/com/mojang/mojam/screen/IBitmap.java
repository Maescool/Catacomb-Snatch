package com.mojang.mojam.screen;

public interface IBitmap {
	
	void clear(int color);
	void blit(Bitmap bitmap, int x, int y);
	int blendPixels(int backgroundColor, int pixelToBlendColor);
	void blit(Bitmap bitmap, int x, int y, int width, int height);
	/***
	 * Draws a Bitmap semi-transparent
	 * @param bitmap image to draw
	 * @param x position on screen
	 * @param y position on screen
	 * @param alpha range from 0x00 (transparent) to 0xff (opaque)
	 */
    void alphaBlit(Bitmap bitmap, int x, int y, int alpha);
    void colorBlit(Bitmap bitmap, int x, int y, int color);
    /***
     * Fills semi-transparent region on screen
     * @param x position on screen
     * @param y position on screen
     * @param width of the region
     * @param height of the region
     * @param color to fill the region
     * @param alpha range from 0x00 (transparent) to 0xff (opaque)
     */
    public void alphaFill(int x, int y, int width, int height, int color, int alpha);
    void fill(int x, int y, int width, int height, int color);
    void adjustBlitArea(Rect blitArea);
    void rectangle(int x, int y, int bw, int bh, int color);
    void setPixel(int x, int y, int color);
	void circle(int centerX, int centerY, int radius, int color);
	void circleFill(int centerX, int centerY, int radius, int color);
	void horizonalLine(int x1, int x2, int y, int color);
}
