package com.mojang.mojam.screen;

import com.mojang.mojam.screen.os.Default;
import java.awt.Color;

public abstract class Bitmap {

	public int w, h;
	public int[] pixels;
	public static String abstractClassName = "com.mojang.mojam.screen.os.default";

	protected void adjustBlitArea(Rect blitArea) {

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

	protected void circle(int centerX, int centerY, int radius, int color) {
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

	protected void circleFill(int centerX, int centerY, int radius, int color) {
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

	protected void horizonalLine(int x1, int x2, int y, int color) {
		if (x1 > x2) {
			int xx = x1;
			x1 = x2;
			x2 = xx;
		}

		for (int xx = x1; xx <= x2; xx++) {
			setPixel(xx, y, color);
		}
	}

	protected void setPixel(int x, int y, int color) {
		pixels[x + y * w] = color;

	}

	protected abstract void initialize(int w, int h);

	protected abstract void setPixels(int[] data);

	public abstract void alphaBlit(Bitmap bitmap, int x, int y, int alpha);

	public abstract void alphaFill(int x, int y, int width, int height, int color, int alpha);

	public abstract int blendPixels(int backgroundColor, int pixelToBlendColor);

	public abstract void blit(Bitmap bitmap, int x, int y);

	public abstract void blit(Bitmap bitmap, int x, int y, int width, int height);

	public abstract void clear(int color);

	public abstract void colorBlit(Bitmap bitmap, int x, int y, int color);

	public abstract Bitmap copy();

	public abstract void fill(int x, int y, int width, int height, int color);

	public abstract void rectangle(int x, int y, int bw, int bh, int color);

	public static Bitmap rangeBitmap(int radius, int color) {
		Bitmap circle = createInstance(radius * 2 + 100, radius * 2 + 100);

		circle.circleFill(radius, radius, radius, color);
		return circle;
	}

	public static Bitmap rectangleBitmap(int x, int y, int x2, int y2, int color) {
		Bitmap rect = createInstance(x2, y2);
		rect.rectangle(x, y, x2, y2, color);
		return rect;
	}

	public static Bitmap scaleBitmap(Bitmap bitmap, int width, int height) {
		Bitmap scaledBitmap = createInstance(width, height);

		int scaleRatioWidth = ((bitmap.w << 16) / width);
		int scaleRatioHeight = ((bitmap.h << 16) / height);

		int i = 0;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				scaledBitmap.pixels[i++] = bitmap.pixels[(bitmap.w * ((y * scaleRatioHeight) >> 16)) + ((x * scaleRatioWidth) >> 16)];
			}
		}

		return scaledBitmap;
	}

	public static Bitmap shrink(Bitmap bitmap) {
		Bitmap newbmp = createInstance(bitmap.w / 2, bitmap.h / 2);
		int[] pix = bitmap.pixels;
		int blarg = 0;
		for (int i = 0; i < pix.length; i++) {
			if (blarg >= newbmp.pixels.length) {
				break;
			}
			if (i % 2 == 0) {
				newbmp.pixels[blarg] = pix[i];
				blarg++;
			}
			if (i % bitmap.w == 0) {
				i += bitmap.w;
			}
		}

		return newbmp;
	}

	public static Bitmap tooltipBitmap(int width, int height) {
		int cRadius = 3;
		int color = Color.black.getRGB();
		Bitmap tooltip = createInstance(width + 3, height + 3);
		tooltip.fill(0, cRadius, width, height - 2 * cRadius, color);
		tooltip.fill(cRadius, 0, width - 2 * cRadius, height, color);
		// draw corner circles
		tooltip.circleFill(cRadius, cRadius, cRadius, color);
		tooltip.circleFill(width - cRadius, cRadius, cRadius, color);
		tooltip.circleFill(width - cRadius, height - cRadius, cRadius, color);
		tooltip.circleFill(cRadius, height - cRadius, cRadius, color);

		return tooltip;
	}

	public static Bitmap createInstance(int width, int height) {
		Bitmap res = null;
		try {
			res = (Bitmap) Class.forName(abstractClassName).newInstance();
		} catch (ClassNotFoundException ex) {
			res = null;
		} catch (InstantiationException ex) {
			res = null;
		} catch (IllegalAccessException ex) {
			res = null;
		}
		if (res == null) {
			res = new Default();
		}
		res.initialize(width, height);
		return res;
	}
}