package com.mojang.mojam.screen;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class Screen implements IBitmap {
	public BufferedImage image;
	public int w, h;
	private Bitmap screen;
	private int xOffset, yOffset;

	public Screen(int w, int h) {
		this.w = w;
		this.h = h;
		screen = Bitmap.getSystemCompatibleBitmap(w, h);
		image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		screen.pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
	}

	public void setOffset(int xOffset, int yOffset) {
		this.xOffset = xOffset;
		this.yOffset = yOffset;
	}

	public void blit(Bitmap bitmap, double x, double y) {
		blit(bitmap, (int) x, (int) y);
	}

	public void blit(Bitmap bitmap, int x, int y) {
		screen.blit(bitmap, x + xOffset, y + yOffset);
	}

	public void blit(Bitmap bitmap, int x, int y, int w, int h) {
		screen.blit(bitmap, x + xOffset, y + yOffset, w, h);
	}
	
	public void alphaBlit(Bitmap bitmap, int x, int y, int alpha) {
		screen.alphaBlit(bitmap, x + xOffset, y + yOffset, alpha);
	}

	public void colorBlit(Bitmap bitmap, double x, double y, int color) {
		colorBlit(bitmap, (int) x, (int) y, color);
	}

	public void colorBlit(Bitmap bitmap, int x, int y, int color) {
		screen.colorBlit(bitmap, x + xOffset, y + yOffset, color);
	}

	public void fill(int x, int y, int width, int height, int color) {
		screen.fill(x + xOffset, y + yOffset, width, height, color);
	}
	
	public void rectangle(int x, int y, int width, int height, int color) {
		screen.rectangle(x + xOffset, y + yOffset, width, height, color);
	}

	public void clear(int color) {
		screen.clear(color);
	}

	public int blendPixels(int backgroundColor, int pixelToBlendColor) {
		return screen.blendPixels(backgroundColor, pixelToBlendColor);
	}

	public void alphaFill(int x, int y, int width, int height, int color, int alpha) {
		screen.alphaFill(x, y, width, height, color, alpha);
	}
	
	public void adjustBlitArea(Rect blitArea) {
		screen.adjustBlitArea(blitArea);
	}

	public void setPixel(int x, int y, int color) {
		screen.setPixel(x, y, color);
	}

	public void circle(int centerX, int centerY, int radius, int color) {
		screen.circle(centerX, centerY, radius, color);
	}

	public void circleFill(int centerX, int centerY, int radius, int color) {
		screen.circleFill(centerX, centerY, radius, color);
	}

	public void horizonalLine(int x1, int x2, int y, int color) {
		screen.horizonalLine(x1, x2, y, color);
	}
}