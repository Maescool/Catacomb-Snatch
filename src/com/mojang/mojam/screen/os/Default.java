package com.mojang.mojam.screen.os;

import com.mojang.mojam.screen.Bitmap;
import com.mojang.mojam.screen.Rect;
import java.awt.Color;
import java.util.Arrays;

public class Default extends Bitmap {

	public Default() {
	}

	public Default(int w, int h, int[] pixels) {
		this.w = w;
		this.h = h;
	}

	public Default(int[][] pixels2D) {
		w = pixels2D.length;
		if (w > 0) {
			h = pixels2D[0].length;
			pixels = new int[w * h];
			for (int y = 0; y < h; y++) {
				for (int x = 0; x < w; x++) {
					pixels[y * w + x] = pixels2D[x][y];
				}
			}
		} else {
			h = 0;
			pixels = new int[0];
		}
	}

	@Override
	protected void initialize(int w, int h) {
		this.w = w;
		this.h = h;
		pixels = new int[w * h];
	}
	@Override
	protected void setPixels(int[] data) {
		this.pixels = data;
	}

	public void alphaBlit(Bitmap bitmap, int x, int y, int alpha) {

		if (alpha == 255) {
			this.blit(bitmap, x, y);
			return;
		}

		Rect blitArea = new Rect(x, y, bitmap.w, bitmap.h);
		adjustBlitArea(blitArea);

		int blitWidth = blitArea.bottomRightX - blitArea.topLeftX;

		for (int yy = blitArea.topLeftY; yy < blitArea.bottomRightY; yy++) {
			int tp = yy * w + blitArea.topLeftX;
			int sp = (yy - y) * bitmap.w + (blitArea.topLeftX - x);
			for (int xx = 0; xx < blitWidth; xx++) {
				int col = bitmap.pixels[sp + xx];
				if (col < 0) {

					int r = (col & 0xff0000);
					int g = (col & 0xff00);
					int b = (col & 0xff);
					col = (alpha << 24) | r | g | b;
					int color = pixels[tp + xx];
					pixels[tp + xx] = this.blendPixels(color, col);
				}
			}
		}
	}

	public void alphaFill(int x, int y, int width, int height, int color, int alpha) {

		if (alpha == 255) {
			this.fill(x, y, width, height, color);
			return;
		}

		Bitmap bmp = createInstance(width, height);
		bmp.fill(0, 0, width, height, color);

		this.alphaBlit(bmp, x, y, alpha);
	}

	public int blendPixels(int backgroundColor, int pixelToBlendColor) {

		int alpha_blend = (pixelToBlendColor >> 24) & 0xff;

		int alpha_background = 256 - alpha_blend;

		int rr = backgroundColor & 0xff0000;
		int gg = backgroundColor & 0xff00;
		int bb = backgroundColor & 0xff;

		int r = (pixelToBlendColor & 0xff0000);
		int g = (pixelToBlendColor & 0xff00);
		int b = (pixelToBlendColor & 0xff);

		r = ((r * alpha_blend + rr * alpha_background) >> 8) & 0xff0000;
		g = ((g * alpha_blend + gg * alpha_background) >> 8) & 0xff00;
		b = ((b * alpha_blend + bb * alpha_background) >> 8) & 0xff;

		return 0xff000000 | r | g | b;
	}

	public void blit(Bitmap bitmap, int x, int y) {

		Rect blitArea = new Rect(x, y, bitmap.w, bitmap.h);
		adjustBlitArea(blitArea);
		int blitWidth = blitArea.bottomRightX - blitArea.topLeftX;

		for (int yy = blitArea.topLeftY; yy < blitArea.bottomRightY; yy++) {
			int tp = yy * w + blitArea.topLeftX;
			int sp = (yy - y) * bitmap.w + (blitArea.topLeftX - x);
			tp -= sp;
			for (int xx = sp; xx < sp + blitWidth; xx++) {
				int col = bitmap.pixels[xx];
				int alpha = (col >> 24) & 0xff;

				if (alpha == 255) {
					pixels[tp + xx] = col;
				} else {
					pixels[tp + xx] = blendPixels(pixels[tp + xx], col);
				}
			}
		}
	}

	public void blit(Bitmap bitmap, int x, int y, int width, int height) {

		Rect blitArea = new Rect(x, y, width, height);
		adjustBlitArea(blitArea);
		int blitWidth = blitArea.bottomRightX - blitArea.topLeftX;

		for (int yy = blitArea.topLeftY; yy < blitArea.bottomRightY; yy++) {
			int tp = yy * w + blitArea.topLeftX;
			int sp = (yy - y) * bitmap.w + (blitArea.topLeftX - x);
			tp -= sp;
			for (int xx = sp; xx < sp + blitWidth; xx++) {
				int col = bitmap.pixels[xx];
				int alpha = (col >> 24) & 0xff;

				if (alpha == 255) {
					pixels[tp + xx] = col;
				} else {
					pixels[tp + xx] = blendPixels(pixels[tp + xx], col);
				}
			}
		}
	}

	public void clear(int color) {
		Arrays.fill(pixels, color);
	}

	public void colorBlit(Bitmap bitmap, int x, int y, int color) {

		Rect blitArea = new Rect(x, y, bitmap.w, bitmap.h);
		adjustBlitArea(blitArea);

		int blitWidth = blitArea.bottomRightX - blitArea.topLeftX;

		int a2 = (color >> 24) & 0xff;
		int a1 = 256 - a2;

		int rr = color & 0xff0000;
		int gg = color & 0xff00;
		int bb = color & 0xff;

		for (int yy = blitArea.topLeftY; yy < blitArea.bottomRightY; yy++) {
			int tp = yy * w + blitArea.topLeftX;
			int sp = (yy - y) * bitmap.w + (blitArea.topLeftX - x);
			for (int xx = 0; xx < blitWidth; xx++) {
				int col = bitmap.pixels[sp + xx];
				if (col < 0) {
					int r = (col & 0xff0000);
					int g = (col & 0xff00);
					int b = (col & 0xff);

					r = ((r * a1 + rr * a2) >> 8) & 0xff0000;
					g = ((g * a1 + gg * a2) >> 8) & 0xff00;
					b = ((b * a1 + bb * a2) >> 8) & 0xff;
					pixels[tp + xx] = 0xff000000 | r | g | b;
				}
			}
		}
	}

	public Bitmap copy() {
		Bitmap rValue = createInstance(this.w, this.h);
		rValue.pixels = this.pixels.clone();
		return rValue;
	}

	public void fill(int x, int y, int width, int height, int color) {

		Rect blitArea = new Rect(x, y, width, height);
		adjustBlitArea(blitArea);

		int blitWidth = blitArea.bottomRightX - blitArea.topLeftX;

		for (int yy = blitArea.topLeftY; yy < blitArea.bottomRightY; yy++) {
			int tp = yy * w + blitArea.topLeftX;
			for (int xx = 0; xx < blitWidth; xx++) {
				pixels[tp + xx] = color;
			}
		}
	}

	public void rectangle(int x, int y, int bw, int bh, int color) {
		int x0 = x;
		int x1 = x + bw;
		int y0 = y;
		int y1 = y + bh;
		if (x0 < 0) {
			x0 = 0;
		}
		if (y0 < 0) {
			y0 = 0;
		}
		if (x1 > w) {
			x1 = w;
		}
		if (y1 > h) {
			y1 = h;
		}

		for (int yy = y0; yy < y1; yy++) {
			setPixel(x0, yy, color);
			setPixel(x1 - 1, yy, color);
		}

		for (int xx = x0; xx < x1; xx++) {
			setPixel(xx, y0, color);
			setPixel(xx, y1 - 1, color);
		}
	}

}