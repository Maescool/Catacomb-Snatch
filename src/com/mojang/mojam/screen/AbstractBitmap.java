/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mojang.mojam.screen;

/**
 *
 * @author Jorge
 */
public interface AbstractBitmap {

	public AbstractBitmap copy();

	public void clear(int color);

	public void blit(AbstractBitmap bitmap, int x, int y);

	public int blendPixels(int backgroundColor, int pixelToBlendColor);

	public void blit(AbstractBitmap bitmap, int x, int y, int width, int height);

	public void alphaBlit(AbstractBitmap bitmap, int x, int y, int alpha);

	public void colorBlit(AbstractBitmap bitmap, int x, int y, int color);

	public void alphaFill(int x, int y, int width, int height, int color, int alpha);

	public void fill(int x, int y, int width, int height, int color);

	public void rectangle(int x, int y, int bw, int bh, int color);

	public AbstractBitmap shrink();

	public AbstractBitmap scaleBitmap(int width, int height);
//	private void adjustBlitArea(Rect blitArea);
//	private void setPixel(int x, int y, int color);
//	private void circle(int centerX, int centerY, int radius, int color);
//	private void circleFill(int centerX, int centerY, int radius, int color);
//	private void horizonalLine(int x1, int x2, int y, int color);
}
