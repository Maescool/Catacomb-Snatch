package com.mojang.mojam.screen;

public interface AbstractScreen {

	public boolean createWindow();

	public void loadResources();

	public void setOffset(int xOffset, int yOffset);

	public AbstractBitmap createBitmap(int w, int h);

	public void blit(AbstractBitmap bitmap, int x, int y);

	public void blit(AbstractBitmap bitmap, int x, int y, int width, int height);

	public void fill(int x, int y, int width, int height, int color);

	public AbstractBitmap load(String pathFile);

	public AbstractBitmap[][] cut(String pathFile, int w, int h);

	public AbstractBitmap[][] cut(String pathFile, int w, int h, int bx, int by);

	public int[][] getColors(AbstractBitmap[][] tiles);

	public int getColor(AbstractBitmap bitmap);

	public AbstractBitmap[][] cutv(String string, int h);

	public AbstractBitmap shrink(AbstractBitmap bitmap);

	public AbstractBitmap scaleBitmap(AbstractBitmap bitmap, int width, int height);
}
