package com.mojang.mojam.screen;

public interface AbstractBitmap {

	public int getWidth();

	public int getHeight();

	public AbstractBitmap copy();

	public void clear(int color);

	public int blendPixels(int backgroundColor, int pixelToBlendColor);

	public void blit(AbstractBitmap bitmap, int x, int y);

	public void blit(AbstractBitmap bitmap, int x, int y, int width, int height);

	public void alphaBlit(AbstractBitmap bitmap, int x, int y, int alpha);

	public void colorBlit(AbstractBitmap bitmap, int x, int y, int color);

	public void alphaFill(int x, int y, int width, int height, int color, int alpha);

	public void fill(int x, int y, int width, int height, int color);

	public void rectangle(int x, int y, int bw, int bh, int color);

	public AbstractBitmap shrink();

	public AbstractBitmap scaleBitmap(int width, int height);
}
