package com.mojang.mojam.screen;

import java.util.Arrays;

public class MojamBitmap implements AbstractBitmap {

	protected int[] pixels;
	protected int w, h;

	public MojamBitmap(int w, int h) {
		this.w = w;
		this.h = h;
		pixels = new int[w * h];
	}

	public MojamBitmap(int w, int h, int[] pixels) {
		this.w = w;
		this.h = h;
		this.pixels = pixels;
	}

	public MojamBitmap(int[][] pixels2D) {
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
	public AbstractBitmap copy() {
		MojamBitmap rValue = new MojamBitmap(this.w, this.h);
		rValue.pixels = this.pixels.clone();
		return rValue;
	}

	@Override
	public void clear(int color) {
		Arrays.fill(pixels, color);
	}

	@Override
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

	@Override
	public void blit(AbstractBitmap bitmap, int x, int y) {
		MojamBitmap mb = (MojamBitmap) bitmap;
		Rect blitArea = new Rect(x, y, mb.w, mb.h);
		adjustBlitArea(blitArea);
		int blitWidth = blitArea.bottomRightX - blitArea.topLeftX;

		for (int yy = blitArea.topLeftY; yy < blitArea.bottomRightY; yy++) {
			int tp = yy * w + blitArea.topLeftX;
			int sp = (yy - y) * mb.w + (blitArea.topLeftX - x);
			tp -= sp;
			for (int xx = sp; xx < sp + blitWidth; xx++) {
				int col = mb.pixels[xx];
				int alpha = (col >> 24) & 0xff;

				if (alpha == 255) {
					pixels[tp + xx] = col;
				} else {
					pixels[tp + xx] = blendPixels(pixels[tp + xx], col);
				}
			}
		}
	}

	@Override
	public void blit(AbstractBitmap bitmap, int x, int y, int width, int height) {
		MojamBitmap mb = (MojamBitmap) bitmap;
		Rect blitArea = new Rect(x, y, width, height);
		adjustBlitArea(blitArea);
		int blitWidth = blitArea.bottomRightX - blitArea.topLeftX;

		for (int yy = blitArea.topLeftY; yy < blitArea.bottomRightY; yy++) {
			int tp = yy * w + blitArea.topLeftX;
			int sp = (yy - y) * mb.w + (blitArea.topLeftX - x);
			tp -= sp;
			for (int xx = sp; xx < sp + blitWidth; xx++) {
				int col = mb.pixels[xx];
				int alpha = (col >> 24) & 0xff;

				if (alpha == 255) {
					pixels[tp + xx] = col;
				} else {
					pixels[tp + xx] = blendPixels(pixels[tp + xx], col);
				}
			}
		}
	}

	@Override
	public void alphaBlit(AbstractBitmap bitmap, int x, int y, int alpha) {
		MojamBitmap mb = (MojamBitmap) bitmap;
		if (alpha == 255) {
			blit(bitmap, x, y);
			return;
		}

		Rect blitArea = new Rect(x, y, mb.w, mb.h);
		adjustBlitArea(blitArea);

		int blitWidth = blitArea.bottomRightX - blitArea.topLeftX;

		for (int yy = blitArea.topLeftY; yy < blitArea.bottomRightY; yy++) {
			int tp = yy * w + blitArea.topLeftX;
			int sp = (yy - y) * mb.w + (blitArea.topLeftX - x);
			for (int xx = 0; xx < blitWidth; xx++) {
				int col = mb.pixels[sp + xx];
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

	@Override
	public void colorBlit(AbstractBitmap bitmap, int x, int y, int color) {
		MojamBitmap mb = (MojamBitmap) bitmap;
		Rect blitArea = new Rect(x, y, mb.w, mb.h);
		adjustBlitArea(blitArea);

		int blitWidth = blitArea.bottomRightX - blitArea.topLeftX;

		int a2 = (color >> 24) & 0xff;
		int a1 = 256 - a2;

		int rr = color & 0xff0000;
		int gg = color & 0xff00;
		int bb = color & 0xff;

		for (int yy = blitArea.topLeftY; yy < blitArea.bottomRightY; yy++) {
			int tp = yy * w + blitArea.topLeftX;
			int sp = (yy - y) * mb.w + (blitArea.topLeftX - x);
			for (int xx = 0; xx < blitWidth; xx++) {
				int col = ((MojamBitmap) bitmap).pixels[sp + xx];
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

	@Override
	public void alphaFill(int x, int y, int width, int height, int color, int alpha) {

		if (alpha == 255) {
			this.fill(x, y, width, height, color);
			return;
		}

		MojamBitmap bmp = new MojamBitmap(width, height);
		bmp.fill(0, 0, width, height, color);

		this.alphaBlit(bmp, x, y, alpha);
	}

	@Override
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

	private void adjustBlitArea(Rect blitArea) {

		if (blitArea.topLeftX < 0) {
			blitArea.topLeftX = 0;
		}
		if (blitArea.topLeftY < 0) {
			blitArea.topLeftY = 0;
		}
		if (blitArea.bottomRightX > w) {
			blitArea.bottomRightX = w;
		}
		if (blitArea.bottomRightY > h) {
			blitArea.bottomRightY = h;
		}
	}

	@Override
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

	private void setPixel(int x, int y, int color) {
		pixels[x + y * w] = color;

	}

	private void circle(int centerX, int centerY, int radius, int color) {
		int d = 3 - (2 * radius);
		int x = 0;
		int y = radius;

		do {
			setPixel(centerX + x, centerY + y, color);
			setPixel(centerX + x, centerY - y, color);
			setPixel(centerX - x, centerY + y, color);
			setPixel(centerX - x, centerY - y, color);
			setPixel(centerX + y, centerY + x, color);
			setPixel(centerX + y, centerY - x, color);
			setPixel(centerX - y, centerY + x, color);
			setPixel(centerX - y, centerY - x, color);

			if (d < 0) {
				d = d + (4 * x) + 6;
			} else {
				d = d + 4 * (x - y) + 10;
				y--;
			}
			x++;
		} while (x <= y);
	}

	public void circleFill(int centerX, int centerY, int radius, int color) {
		int d = 3 - (2 * radius);
		int x = 0;
		int y = radius;

		do {
			horizonalLine(centerX + x, centerX - x, centerY + y, color);
			horizonalLine(centerX + x, centerX - x, centerY - y, color);
			horizonalLine(centerX + y, centerX - y, centerY + x, color);
			horizonalLine(centerX + y, centerX - y, centerY - x, color);

			if (d < 0) {
				d = d + (4 * x) + 6;
			} else {
				d = d + 4 * (x - y) + 10;
				y--;
			}
			x++;
		} while (x <= y);
	}

	private void horizonalLine(int x1, int x2, int y, int color) {
		if (x1 > x2) {
			int xx = x1;
			x1 = x2;
			x2 = xx;
		}

		for (int xx = x1; xx <= x2; xx++) {
			setPixel(xx, y, color);
		}
	}

	@Override
	public AbstractBitmap shrink() {
		MojamBitmap newbmp = new MojamBitmap(w / 2, h / 2);
		int[] pix = pixels;
		int blarg = 0;
		for (int i = 0; i < pix.length; i++) {
			if (blarg >= newbmp.pixels.length) {
				break;
			}
			if (i % 2 == 0) {
				newbmp.pixels[blarg] = pix[i];
				blarg++;
			}
			if (i % w == 0) {
				i += w;
			}
		}

		return newbmp;
	}

	@Override
	public AbstractBitmap scaleBitmap(int width, int height) {
		MojamBitmap scaledBitmap = new MojamBitmap(width, height);

		int scaleRatioWidth = ((w << 16) / width);
		int scaleRatioHeight = ((h << 16) / height);

		int i = 0;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				scaledBitmap.pixels[i++] = pixels[(w * ((y * scaleRatioHeight) >> 16)) + ((x * scaleRatioWidth) >> 16)];
			}
		}

		return scaledBitmap;
	}

	@Override
	public int getWidth() {
		return w;
	}

	@Override
	public int getHeight() {
		return h;
	}
}