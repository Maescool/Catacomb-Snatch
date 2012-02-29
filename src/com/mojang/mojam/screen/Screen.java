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
	
	public void opacityBlit(Bitmap bitmap, int x, int y, int opacity) {
		super.opacityBlit(bitmap, x + xOffset, y + yOffset, opacity);
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
	public void screenResolution(int width, int height){
		int[] screenWidth = 
		{640, 640, 800, 1024, 1152, 1280, 1280, 1280, 1366, 1400, 1440, 1600, 1680, 1920, 1920, 2560, 2560};
		int[] screenHeight = 
		{350, 480, 600, 768,864, 720, 800, 1024, 768, 1050, 900, 900, 1050, 1080, 1200, 1440, 1600};
		super.Screen(screenWidth[width],screenHeight[height]);
	}
}