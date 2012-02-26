package com.mojang.mojam.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import com.mojang.mojam.screen.Bitmap;

public class FontFactory {

	private static HashMap<Character, Bitmap> characterCache = new HashMap<Character, Bitmap>();
	private static HashMap<Character, Double> characterHeightOffset = new HashMap<Character, Double>();

	private static Color[] goldGradient = {
		new Color(241, 216, 145),
		new Color(242, 236, 153),
		new Color(250, 250, 214),
		new Color(255, 255, 255),
		new Color(250, 250, 214),
		new Color(234, 221, 91),
		new Color(240, 195, 137) };

	public static Bitmap getFontCharacter(char character, int fontSize) {
		if (characterCache.containsKey(character)) {
			return characterCache.get(character);
		}

		java.awt.Font font = new java.awt.Font("SansSerif", java.awt.Font.BOLD, fontSize);
		BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = image.createGraphics();
		FontRenderContext frc = graphics.getFontRenderContext();
		TextLayout layout = new TextLayout(Character.toString(character), font, frc);
		TextLayout layoutStandardLetter = new TextLayout(Character.toString('O'), font, frc);
		Rectangle2D bounds = layout.getBounds();
		double heightOffset = bounds.getY() - layoutStandardLetter.getBounds().getY();
		characterHeightOffset.put(character, heightOffset);
		System.out.println(character + ": " + heightOffset);

		int width = (int) (bounds.getWidth() + 0.5) + 10;
		int height = (int) (bounds.getHeight() + 0.5) + 10;

		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		graphics = image.createGraphics();

		graphics.setFont(font);

		graphics.setColor(Color.BLACK);
		graphics.drawString(Character.toString(character), 3, height - 5);
		Color mainLetterColor = Color.MAGENTA;
		graphics.setColor(mainLetterColor);
		graphics.drawString(Character.toString(character), 2, height - 6);

		int[][] pixels = new int[width][height];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				pixels[x][y] = image.getRGB(x, y);
			}
		}

		pixels = automaticCrop(pixels);
		width = pixels.length;
		if (width == 0) {
			return new Bitmap(pixels);
		}
		height = pixels[0].length;

		Color[] gradient = goldGradient;
		int row = 0;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (pixels[x][y] != 0 && pixels[x][y] != Color.black.getRGB()) {
					pixels[x][y] = gradient[row].getRGB();
				}
			}
			row = Math.min(row + 1, gradient.length - 1);
		}

		Bitmap characterBitmap = new Bitmap(pixels);
		characterCache.put(character, characterBitmap);

		return characterBitmap;
	}

	public static double getHeightOffset(char character) {
		if (characterHeightOffset.containsKey(character)) {
			return characterHeightOffset.get(character);
		}
		return 0;
	}

	private static int[][] automaticCrop(int[][] pixels) {
		int width = pixels.length;
		int height = pixels[0].length;
		int emptyRowsTop = 0;

		FindTop: for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (pixels[x][y] != 0) {
					break FindTop;
				}
			}
			emptyRowsTop++;
		}

		int emptyRowsBottom = 0;
		FindBottom: for (int y = height - 1; y >= 0; y--) {
			for (int x = 0; x < width; x++) {
				if (pixels[x][y] != 0) {
					break FindBottom;
				}
			}
			emptyRowsBottom++;
		}

		int emptyRowsLeft = 0;
		FindLeft: for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (pixels[x][y] != 0) {
					break FindLeft;
				}
			}
			emptyRowsLeft++;
		}

		int emptyRowsRight = 0;
		FindRight: for (int x = width - 1; x >= 0; x--) {
			for (int y = 0; y < height; y++) {
				if (pixels[x][y] != 0) {
					break FindRight;
				}
			}
			emptyRowsRight++;
		}

		if (emptyRowsBottom + emptyRowsTop >= height || emptyRowsLeft + emptyRowsRight >= width) {
			return new int[0][0];
		}
		int[][] pixelsCropped = new int[width - emptyRowsLeft - emptyRowsRight][height
				- emptyRowsTop - emptyRowsBottom];
		for (int y = emptyRowsTop; y < height - emptyRowsBottom; y++) {
			for (int x = emptyRowsLeft; x < width - emptyRowsRight; x++) {
				pixelsCropped[x - emptyRowsLeft][y - emptyRowsTop] = pixels[x][y];
			}
		}
		return pixelsCropped;
	}
}
