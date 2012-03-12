package com.mojang.mojam.screen;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class Screen {
	private Bitmap bm;
	public BufferedImage image;
	private int xOffset, yOffset;

	public Screen(int w, int h) {
		image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		bm = Bitmap.createInstance(w, h);
		bm.setPixels(((DataBufferInt) image.getRaster().getDataBuffer()).getData());
	}

	public void setOffset(int xOffset, int yOffset) {
		this.xOffset = xOffset;
		this.yOffset = yOffset;
	}

	public void blit(Bitmap bitmap, double x, double y) {
		blit(bitmap, (int) x, (int) y);
	}

	public void blit(Bitmap bitmap, int x, int y) {
		bm.blit(bitmap, x + xOffset, y + yOffset);
	}

	public void blit(Bitmap bitmap, int x, int y, int w, int h) {
		bm.blit(bitmap, x + xOffset, y + yOffset, w, h);
	}
	
	public void alphaBlit(Bitmap bitmap, int x, int y, int alpha) {
		bm.alphaBlit(bitmap, x + xOffset, y + yOffset, alpha);
	}

	public void colorBlit(Bitmap bitmap, double x, double y, int color) {
		colorBlit(bitmap, (int) x, (int) y, color);
	}

	public void colorBlit(Bitmap bitmap, int x, int y, int color) {
		bm.colorBlit(bitmap, x + xOffset, y + yOffset, color);
	}

	public void fill(int x, int y, int width, int height, int color) {
		bm.fill(x + xOffset, y + yOffset, width, height, color);
	}
	
	public void rectangle(int x, int y, int width, int height, int color) {
		bm.rectangle(x + xOffset, y + yOffset, width, height, color);
	}
}