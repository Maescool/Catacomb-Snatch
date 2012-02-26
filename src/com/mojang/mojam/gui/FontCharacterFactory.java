package com.mojang.mojam.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import com.mojang.mojam.screen.Bitmap;

public class FontCharacterFactory {

	java.awt.Font systemFont;
	private Color[] gradient;
	private Color shadowColor;
	
	private HashMap<Character, Bitmap> characterCache = new HashMap<Character, Bitmap>();
	private HashMap<Character, Integer> characterHeightOffset = new HashMap<Character, Integer>();
	
	public FontCharacterFactory(java.awt.Font systemFont, Color[] gradient, Color shadowColor) {
		this.systemFont = systemFont;
		this.gradient = gradient;
		this.shadowColor = shadowColor;
	}

	public Bitmap getFontCharacter(char character) {
		if (characterCache.containsKey(character)) {
			return characterCache.get(character);
		}
		
		int fontSize = systemFont.getSize();
		BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = image.createGraphics();
		
		int width = 3*fontSize;
		int height = 3*fontSize;

		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		graphics = image.createGraphics();
		graphics.setFont(systemFont);

		int positionX = fontSize;
		int positionY = 2*fontSize;
		if(shadowColor != null) {
			graphics.setColor(shadowColor);
			graphics.drawString(Character.toString(character), positionX+1, positionY+1);
		}
		
		Color mainLetterColor;
		if(shadowColor!=Color.MAGENTA) { // Any color will do, as long as it's different from the shadow color
			mainLetterColor = Color.MAGENTA;
		} else {
			mainLetterColor = Color.YELLOW;
		}
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
		int hardcodedOffset = (shadowColor!=null) ? 3 : 2;
		characterHeightOffset.put(character, emptyRowsTop-fontSize-hardcodedOffset);
		
		pixels = automaticCrop(pixels);
		
		width = pixels.length;
		if (width == 0) {
			return new Bitmap(pixels);
		}
		height = pixels[0].length;

		int row = 0;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (pixels[x][y] != 0 && (shadowColor==null || pixels[x][y] != shadowColor.getRGB())) {
					pixels[x][y] = gradient[row].getRGB();
				}
			}
			row = Math.min(row + 1, gradient.length - 1);
		}

		Bitmap characterBitmap = new Bitmap(pixels);
		characterCache.put(character, characterBitmap);

		return characterBitmap;
	}

	public int getHeightOffset(char character) {
		if (!characterHeightOffset.containsKey(character)) {
			getFontCharacter(character);
		}
		return characterHeightOffset.get(character);
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
