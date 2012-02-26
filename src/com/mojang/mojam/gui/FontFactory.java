package com.mojang.mojam.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import com.mojang.mojam.screen.Bitmap;

public class FontFactory {

	private static HashMap<String, Bitmap> characterCache = new HashMap<String, Bitmap>();
	private static HashMap<String, Integer> characterHeightOffset = new HashMap<String, Integer>();

	private static Color[] goldGradient = {
		new Color(241, 216, 145),
		new Color(242, 236, 153),
		new Color(250, 250, 214),
		new Color(255, 255, 255),
		new Color(250, 250, 214),
		new Color(234, 221, 91),
		new Color(240, 195, 137) };

	public static Bitmap getFontCharacter(char character, int fontSize) {
		String key = makeKey(character, fontSize);
		if (characterCache.containsKey(key)) {
			return characterCache.get(key);
		}

		java.awt.Font font = new java.awt.Font("SansSerif", java.awt.Font.BOLD, fontSize);
		BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = image.createGraphics();
		
		int width = 3*fontSize;
		int height = 3*fontSize;

		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		graphics = image.createGraphics();
		graphics.setFont(font);

		int positionX = fontSize;
		int positionY = 2*fontSize;
		Color shadowColor = Color.BLACK;
		graphics.setColor(shadowColor);
		graphics.drawString(Character.toString(character), positionX+1, positionY+1);
		Color mainLetterColor = Color.MAGENTA;
		graphics.setColor(mainLetterColor);
		graphics.drawString(Character.toString(character), positionX, positionY);

		int[][] pixels = new int[width][height];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				pixels[x][y] = image.getRGB(x, y);
			}
		}

		int emptyRowsTop = 0;
		FindTop: for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (pixels[x][y] != 0) {
					break FindTop;
				}
			}
			emptyRowsTop++;
		}
		int hardcodedOffset = 3;
		characterHeightOffset.put(key, emptyRowsTop-fontSize-hardcodedOffset);
		
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
				if (pixels[x][y] != 0 && pixels[x][y] != shadowColor.getRGB()) {
					pixels[x][y] = gradient[row].getRGB();
				}
			}
			row = Math.min(row + 1, gradient.length - 1);
		}

		Bitmap characterBitmap = new Bitmap(pixels);
		characterCache.put(key, characterBitmap);

		return characterBitmap;
	}

	public static int getHeightOffset(char character, int fontSize) {
		String key = makeKey(character, fontSize);
		if (!characterHeightOffset.containsKey(key)) {
			getFontCharacter(character, fontSize);
		}
		return characterHeightOffset.get(key);
	}
	
	private static String makeKey(char character, int fontSize){
		return character + ":" + fontSize;
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
