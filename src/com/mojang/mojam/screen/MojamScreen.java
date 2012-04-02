package com.mojang.mojam.screen;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

public class MojamScreen extends MojamBitmap implements AbstractScreen {

	public BufferedImage image;
	protected int xOffset, yOffset;

	@Override
	public void setOffset(int xOffset, int yOffset) {
		this.xOffset = xOffset;
		this.yOffset = yOffset;
	}

	public MojamScreen(int w, int h) {
		super(w, h);
		image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
	}

	@Override
	public void blit(AbstractBitmap bitmap, double x, double y) {
		blit(bitmap, (int) x, (int) y);
	}

	@Override
	public void blit(AbstractBitmap bitmap, int x, int y) {
		super.blit(bitmap, x + xOffset, y + yOffset);
	}

	@Override
	public void blit(AbstractBitmap bitmap, int x, int y, int w, int h) {
		super.blit(bitmap, x + xOffset, y + yOffset, w, h);
	}

	@Override
	public void alphaBlit(AbstractBitmap bitmap, int x, int y, int alpha) {
		super.alphaBlit(bitmap, x + xOffset, y + yOffset, alpha);
	}

	@Override
	public void colorBlit(AbstractBitmap bitmap, double x, double y, int color) {
		colorBlit(bitmap, (int) x, (int) y, color);
	}

	@Override
	public void colorBlit(AbstractBitmap bitmap, int x, int y, int color) {
		super.colorBlit(bitmap, x + xOffset, y + yOffset, color);
	}

	@Override
	public void fill(int x, int y, int width, int height, int color) {
		super.fill(x + xOffset, y + yOffset, width, height, color);
	}

	@Override
	public void rectangle(int x, int y, int width, int height, int color) {
		super.rectangle(x + xOffset, y + yOffset, width, height, color);
	}

	@Override
	public AbstractBitmap rectangleBitmap(int x, int y, int x2, int y2, int color) {
		MojamBitmap rect = new MojamBitmap(x2, y2);
		rect.rectangle(x, y, x2, y2, color);
		return rect;
	}

	@Override
	public AbstractBitmap rangeBitmap(int radius, int color) {
		MojamBitmap circle = new MojamBitmap(radius * 2 + 100, radius * 2 + 100);

		circle.circleFill(radius, radius, radius, color);
		return circle;
	}

	@Override
	public AbstractBitmap tooltipBitmap(int width, int height) {
		int cRadius = 3;
		int color = Color.black.getRGB();
		MojamBitmap tooltip = new MojamBitmap(width + 3, height + 3);
		tooltip.fill(0, cRadius, width, height - 2 * cRadius, color);
		tooltip.fill(cRadius, 0, width - 2 * cRadius, height, color);
		// draw corner circles
		tooltip.circleFill(cRadius, cRadius, cRadius, color);
		tooltip.circleFill(width - cRadius, cRadius, cRadius, color);
		tooltip.circleFill(width - cRadius, height - cRadius, cRadius, color);
		tooltip.circleFill(cRadius, height - cRadius, cRadius, color);

		return tooltip;
	}

	@Override
	public void loadResources() {
		Art.loadAllResources(this);
	}

	@Override
	public AbstractBitmap createBitmap(int w, int h) {
		return new MojamBitmap(w, h);
	}

	@Override
	public AbstractBitmap createBitmap(int[][] pixels2D) {
		return new MojamBitmap(pixels2D);
	}

	@Override
	public AbstractBitmap shrink(AbstractBitmap bitmap) {
		return bitmap.shrink();
	}

	@Override
	public AbstractBitmap scaleBitmap(AbstractBitmap bitmap, int width, int height) {
		return bitmap.scaleBitmap(width, height);
	}

	@Override
	public AbstractBitmap load(String pathFile) {
		try {
			BufferedImage bi = ImageIO.read(MojamScreen.class.getResource(pathFile));
			return load(bi);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public AbstractBitmap load(BufferedImage image) {
		if (image == null) return null;
		int width = image.getWidth();
		int height = image.getHeight();

		return new MojamBitmap(width, height,image.getRGB(0, 0, width, height, null, 0, width));
	}

	@Override
	public AbstractBitmap[][] cut(String pathFile, int w, int h) {
		return cut(pathFile, w, h, 0, 0);
	}

	@Override
	public AbstractBitmap[][] cut(String pathFile, int w, int h, int bx, int by) {
		try {
			BufferedImage bi = ImageIO.read(MojamScreen.class.getResource(pathFile));

			int xTiles = (bi.getWidth() - bx) / w;
			int yTiles = (bi.getHeight() - by) / h;

			MojamBitmap[][] result = new MojamBitmap[xTiles][yTiles];

			for (int x = 0; x < xTiles; x++) {
				for (int y = 0; y < yTiles; y++) {
					result[x][y] = new MojamBitmap(w, h);
					bi.getRGB(bx + x * w, by + y * h, w, h,
						result[x][y].pixels, 0, w);
				}
			}

			return result;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public int[][] getColors(AbstractBitmap[][] tiles) {
		int[][] result = new int[tiles.length][tiles[0].length];
		for (int y = 0; y < tiles[0].length; y++) {
			for (int x = 0; x < tiles.length; x++) {
				result[x][y] = getColor(tiles[x][y]);
			}
		}
		return result;
	}

	@Override
	public int getColor(AbstractBitmap bitmap) {
		MojamBitmap mb = (MojamBitmap) bitmap;
		int r = 0;
		int g = 0;
		int b = 0;
		for (int i = 0; i < mb.pixels.length; i++) {
			int col = mb.pixels[i];
			r += (col >> 16) & 0xff;
			g += (col >> 8) & 0xff;
			b += (col) & 0xff;
		}

		r /= mb.pixels.length;
		g /= mb.pixels.length;
		b /= mb.pixels.length;

		return 0xff000000 | r << 16 | g << 8 | b;
	}

	@Override
	public MojamBitmap[][] cutv(String string, int h) {
		try {
			BufferedImage bi = ImageIO.read(MojamScreen.class.getResource(string));

			int yTiles = bi.getHeight() / h;

			int xTiles = 0;
			MojamBitmap[][] result = new MojamBitmap[yTiles][];
			for (int y = 0; y < yTiles; y++) {
				List<MojamBitmap> row = new ArrayList<MojamBitmap>();
				int xCursor = 0;
				while (xCursor < bi.getWidth()) {
					int w = 0;
					while (xCursor + w < bi.getWidth() && bi.getRGB(xCursor + w, y * h) != 0xffed1c24) {
						w++;
					}
					if (w > 0) {
						MojamBitmap bitmap = new MojamBitmap(w, h);
						bi.getRGB(xCursor, y * h, w, h, bitmap.pixels, 0, w);
						row.add(bitmap);
					}
					xCursor += w + 1;
				}
				if (xTiles < row.size()) {
					xTiles = row.size();
				}
				result[y] = row.toArray(new MojamBitmap[0]);
			}

			MojamBitmap[][] resultT = new MojamBitmap[xTiles][yTiles];
			for (int x = 0; x < xTiles; x++) {
				for (int y = 0; y < yTiles; y++) {
					try {
						resultT[x][y] = result[y][x];
					} catch (IndexOutOfBoundsException e) {
						resultT[x][y] = null;
					}
				}
			}

			return resultT;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}