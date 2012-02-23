package com.mojang.mojam.gui;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.MouseButtons;
import com.mojang.mojam.level.Level;
import com.mojang.mojam.level.LevelInformation;
import com.mojang.mojam.level.gamemode.GameMode;
import com.mojang.mojam.network.TurnSynchronizer;
import com.mojang.mojam.screen.Bitmap;
import com.mojang.mojam.screen.Screen;

public class LevelButton extends ClickableComponent {

	private int id;
	private Bitmap minimap;
	private LevelInformation levelInfo;

	public static final int WIDTH = 140;
	public static final int HEIGHT = 84;

	/**
	 * Generates a minimap bitmap
	 * 
	 * @param mapfile
	 *            path to resource (same as with Level.class)
	 * @throws IOException
	 *             map file not found?
	 */
	public LevelButton(int id, LevelInformation levelInfo, int x, int y) {
		super(x, y, WIDTH, HEIGHT);

		this.id = id;
		this.levelInfo = levelInfo;

		buildMinimap();
	}

	/**
	 * Builds the minimap, loads resource specified by this.mapfile
	 * 
	 * @return build successful
	 */
	private boolean buildMinimap() {

		// back it up and use a local new one instead, just to make sure
		Random backupRandom = TurnSynchronizer.synchedRandom;
		TurnSynchronizer.synchedRandom = new Random();
		
		// load level
		Level l;
		try {
			l = new GameMode().generateLevel(levelInfo);
		} catch (IOException e) {
			return false;
		}

		int w = l.width;
		int h = l.height;
		
		minimap = new Bitmap(w, h);

		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				minimap.pixels[x + y * w] = l.getTile(x, y).minimapColor;
			}
		}

		TurnSynchronizer.synchedRandom = backupRandom;

		return true;
	}

	public int getId() {
		return id;
	}

	private static Bitmap background[] = new Bitmap[3];
	static {
		background[0] = new Bitmap(WIDTH, HEIGHT);
		background[0].fill(0, 0, WIDTH, HEIGHT, 0xff522d16);
		background[0].fill(1, 1, WIDTH-2, HEIGHT-2, 0);
		background[1] = new Bitmap(WIDTH, HEIGHT);
		background[1].fill(0, 0, WIDTH, HEIGHT, 0xff26150a);
		background[1].fill(1, 1, WIDTH-2, HEIGHT-2, 0);
		background[2] = new Bitmap(WIDTH, HEIGHT);
		background[2].fill(0, 0, WIDTH, HEIGHT, 0xff26150a);
		background[2].fill(1, 1, WIDTH-2, HEIGHT-2, 0xff3a210f);
	}

	@Override
	public void render(Screen screen) {

		// render background
		screen.blit(background[isPressed() ? 1 : (isActive ? 2 : 0)], getX(), getY());
		
		// render minimap
		if (minimap != null) {
			screen.blit(minimap, getX() + (getWidth() - minimap.w) / 2, getY() + 4);

			// map name
			Font.drawCentered(screen, levelInfo.levelName, getX() + getWidth() / 2, getY() + 4 + minimap.h + 8);
		} else {
			Font.setFont("red");
			Font.drawCentered(screen, levelInfo.levelName, getX() + getWidth() / 2, getY() + 4 + 32);
			Font.setFont("");
		}
	}

	@Override
	protected void clicked(MouseButtons mouseButtons) {
		isActive = true;
	}

	private boolean isActive = false;

	public void setActive(boolean active) {
		isActive = active;
	}
}
