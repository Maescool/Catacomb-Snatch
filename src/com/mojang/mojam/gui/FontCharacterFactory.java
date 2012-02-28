package com.mojang.mojam.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import com.mojang.mojam.screen.Bitmap;

public class FontCharacterFactory {

	private java.awt.Font systemFont;
	private java.awt.Font fallbackFont;
	private Color[] gradient;
	private Color shadowColor;
	private int heightOffset;
	
	private HashMap<Character, Bitmap> characterCache = new HashMap<Character, Bitmap>();
	private HashMap<Character, Integer> characterHeightOffset = new HashMap<Character, Integer>();
	
	public FontCharacterFactory(java.awt.Font systemFont, java.awt.Font fallbackFont, Color[] gradient, Color shadowColor, int heightOffset) {
		this.systemFont = systemFont;
		this.fallbackFont = fallbackFont;
		this.gradient = gradient;
		this.shadowColor = shadowColor;
		this.heightOffset = heightOffset;
	}

	public Bitmap getFontCharacter(char character) {
		if (characterCache.containsKey(character)) {
			return characterCache.get(character);
		}
		
		java.awt.Font font;
		if (systemFont.canDisplay(character)) {
			font = systemFont;
		} else {
			font = fallbackFont;
		}
		
		int fontSize = font.getSize();
		int width = 3*fontSize;
		int height = 3*fontSize;
		int positionX = fontSize;
		int positionY = 2*fontSize;
		
		BufferedImage mainImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D mainGraphics = mainImage.createGraphics();
		mainGraphics.setFont(font);
		mainGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
		Color mainLetterColor = Color.MAGENTA;
		mainGraphics.setColor(mainLetterColor);
		mainGraphics.drawString(Character.toString(character), positionX, positionY);
		
		int[][] pixels = new int[width][height];
		int gradientRow = gradient.length - 1;
		for (int y = height-1; y >= 0; y--) {
			for (int x = width-1; x >= 0 ; x--) {
				if (mainImage.getRGB(x, y)!=0) {
					pixels[x][y] = gradient[gradientRow].getRGB();
				} else if (x>0 && y>0 && mainImage.getRGB(x-1, y-1)!=0) {
					pixels[x][y] = shadowColor.getRGB();
				}
			}
			if (y < positionY) {
				gradientRow = Math.max(gradientRow - 1, 0);
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
		characterHeightOffset.put(character, emptyRowsTop-fontSize-heightOffset);
		
		pixels = automaticCrop(pixels);
		
		width = pixels.length;
		if (width == 0) {
			return new Bitmap(pixels);
		}
		height = pixels[0].length;

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
