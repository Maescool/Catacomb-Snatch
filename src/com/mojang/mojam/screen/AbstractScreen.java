package com.mojang.mojam.screen;

public interface AbstractScreen {

	public void clear(int color);

	public int getWidth();

	public int getHeight();

	public boolean createWindow();

	public void loadResources();

	public void setOffset(int xOffset, int yOffset);

	public AbstractBitmap createBitmap(int w, int h);

	public AbstractBitmap createBitmap(int[][] pixels2D);

	public void blit(AbstractBitmap bitmap, int x, int y);

	public void blit(AbstractBitmap bitmap, int x, int y, int width, int height);

	public void colorBlit(AbstractBitmap bitmap, int x, int y, int color);

	public void fill(int x, int y, int width, int height, int color);

	public void alphaBlit(AbstractBitmap bitmap, int x, int y, int alpha);

	public void alphaFill(int x, int y, int width, int height, int color, int alpha);

	public AbstractBitmap load(String pathFile);

	public AbstractBitmap[][] cut(String pathFile, int w, int h);

	public AbstractBitmap[][] cut(String pathFile, int w, int h, int bx, int by);

	public int[][] getColors(AbstractBitmap[][] tiles);

	public int getColor(AbstractBitmap bitmap);

	public AbstractBitmap[][] cutv(String string, int h);

	public AbstractBitmap shrink(AbstractBitmap bitmap);

	public AbstractBitmap scaleBitmap(AbstractBitmap bitmap, int width, int height);
	public AbstractBitmap rectangleBitmap(int x, int y, int x2, int y2, int color) ;
	public AbstractBitmap rangeBitmap(int radius, int color);
	public AbstractBitmap tooltipBitmap(int width, int height);
	public void rectangle(int x, int y, int bw, int bh, int color);
}
