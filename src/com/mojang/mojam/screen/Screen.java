package com.mojang.mojam.screen;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class Screen extends Bitmap {
	public BufferedImage image;
	private int xOffset, yOffset;

	public Screen(int w, int h) {
		super(w, h);
		image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
	}

	public void setOffset(int xOffset, int yOffset) {
		this.xOffset = xOffset;
		this.yOffset = yOffset;
	}

	public void blit(Bitmap bitmap, double x, double y) {
		blit(bitmap, (int) x, (int) y);
	}

	public void blit(Bitmap bitmap, int x, int y) {
		super.blit(bitmap, x + xOffset, y + yOffset);
	}

	public void blit(Bitmap bitmap, int x, int y, int w, int h) {
		super.blit(bitmap, x + xOffset, y + yOffset, w, h);
	}
	
	public void alphaBlit(Bitmap bitmap, int x, int y, int alpha) {
		super.alphaBlit(bitmap, x + xOffset, y + yOffset, alpha);
	}

	public void colorBlit(Bitmap bitmap, double x, double y, int color) {
		colorBlit(bitmap, (int) x, (int) y, color);
	}

	public void colorBlit(Bitmap bitmap, int x, int y, int color) {
		super.colorBlit(bitmap, x + xOffset, y + yOffset, color);
	}

	public void fill(int x, int y, int width, int height, int color) {
		super.fill(x + xOffset, y + yOffset, width, height, color);
	}
	
	public void rectangle(int x, int y, int width, int height, int color) {
		super.rectangle(x + xOffset, y + yOffset, width, height, color);
	}
}