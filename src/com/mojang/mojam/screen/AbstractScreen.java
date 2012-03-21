package com.mojang.mojam.screen;

public interface AbstractScreen {
//
//	public int w, h;
//
//	public AbstractScreen(int w, int h) {
//		this.w = w;
//		this.h = h;
//	}
//
//	protected int xOffset, yOffset;
//	public void setOffset(int xOffset, int yOffset) {
//		this.xOffset = xOffset;
//		this.yOffset = yOffset;
//	}

	public boolean createWindow();

	public void loadResources();

	public void setOffset(int xOffset, int yOffset);

	public AbstractBitmap createBitmap(int w, int h);

	public AbstractBitmap load(String pathFile);
	public AbstractBitmap[][] cut(String pathFile, int w, int h);
	public AbstractBitmap[][] cut(String pathFile, int w, int h, int bx, int by);
	public int[][] getColors(AbstractBitmap[][] tiles) ;
	public int getColor(AbstractBitmap bitmap);
	public MojamBitmap[][] cutv(String string, int h);

	public AbstractBitmap shrink(AbstractBitmap bitmap);

	public AbstractBitmap scaleBitmap(AbstractBitmap bitmap, int width, int height);
}
