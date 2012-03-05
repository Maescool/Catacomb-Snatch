package com.mojang.mojam.gui;

import java.io.IOException;
import java.util.Random;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.MouseButtons;
import com.mojang.mojam.level.Level;
import com.mojang.mojam.level.LevelInformation;
import com.mojang.mojam.level.gamemode.GameMode;
import com.mojang.mojam.network.TurnSynchronizer;
import com.mojang.mojam.screen.Bitmap;
import com.mojang.mojam.screen.Screen;

/**
 * A LevelButton is a button with a level minimap drawn on it.
 */
public class LevelButton extends ClickableComponent {

	public static final int WIDTH = 140;
	public static final int HEIGHT = 84;
	public static final int MAXDIM = 64;

	private int id;
	private Bitmap minimap, displaymap;

    private final int MAX_LABEL_LENGTH = 15;
    
    private final String levelName;

    private boolean largeMap;
	private boolean isActive = false;
	private int xScroll, yScroll;
	
	// Background bitmaps for pressed/unpressed/inactive state
	private static Bitmap background[] = new Bitmap[3];
	
	// Initialize background bitmaps
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
   
	/**
	 * Constructor
	 * 
	 * @param id Button id
	 * @param levelInfo Level info
	 * @param x X coordinate
	 * @param y Y coordinate
	 */
	public LevelButton(int id, LevelInformation levelInfo, int x, int y) {
		super(x, y, WIDTH, HEIGHT);

		this.id = id;
		this.levelName = levelInfo.levelName;

		buildMinimap(levelInfo);
		redrawDisplaymap();
	}

	/**
	 * Builds the minimap
	 * 
	 * @param levelInfo Level information
	 * @return True on success, false on error
	 */
	private boolean buildMinimap(LevelInformation levelInfo) {
		Random backupRandom = TurnSynchronizer.synchedRandom;
		TurnSynchronizer.synchedRandom = new Random();
		
		// Load level
		Level l;
		try {
			l = new GameMode().generateLevel(levelInfo);
		} catch (IOException e) {
			return false;
		}

		int w = l.width;
		int h = l.height;
		
		// Render the level minimap into a bitmap
		minimap = new Bitmap(w, h);
		largeMap = w > MAXDIM || h > MAXDIM;
		displaymap = new Bitmap(MAXDIM, MAXDIM);
		
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				minimap.pixels[x + (y * w)] = l.getTile(x, y).minimapColor;
			}
		}

		TurnSynchronizer.synchedRandom = backupRandom;

		return true;
	}

	public int getId() {
		return id;
	}

	@Override
	public void render(Screen screen) {

		// Render background
		screen.blit(background[isPressed() ? 1 : (isActive ? 2 : 0)], getX(), getY());
		
		// Render minimap
		if (minimap != null) {
			screen.blit(largeMap ? displaymap : minimap, getX() + (getWidth() - MAXDIM) / 2, getY() + 4, MAXDIM, MAXDIM);
			
			// map name
			Font.defaultFont().draw(screen, trimToFitButton(levelName), getX() + getWidth() / 2,
					getY() + 4 + MAXDIM + 8, Font.Align.CENTERED);
		} else {
			Font.FONT_RED.draw(screen, trimToFitButton(levelName), getX() + getWidth() / 2,
					getY() + 4 + 32, Font.Align.CENTERED);
		}
	}
	
	public void redrawDisplaymap(){
		if(!largeMap) return;

		for (int y = 0; y < MAXDIM; y++) {
			for (int x = 0; x < MAXDIM; x++) {
				displaymap.pixels[x + y * MAXDIM] = minimap.pixels[(x + xScroll) + (y + yScroll) * minimap.w];
			}
		}
	}

	@Override
	public void tick(MouseButtons mb){
		super.tick(mb);
		int relx = mb.getX() / 2 - (getX() + (getWidth() - MAXDIM) / 2);
		int rely = mb.getY() / 2 - (getY() + 4);
		boolean changed = false;
		if(relx > 0 && rely > 0 && relx < MAXDIM && rely < MAXDIM){
			int i = 18;
			int j = 1;
			if(relx < i){
				xScroll -= j;
				changed = true;
			} else if(relx > MAXDIM-i){
				xScroll += j;
				changed = true;
			}
			if(rely < i){
				yScroll -= j;
				changed = true;
			} else if(rely > MAXDIM-i){
				yScroll += j;
				changed = true;
			}
		}
		if(changed) {
			xScroll = MojamComponent.clampi(xScroll, 0, minimap.w-MAXDIM);
			yScroll = MojamComponent.clampi(yScroll, 0, minimap.h-MAXDIM);
			redrawDisplaymap();
		}
	}

	@Override
	protected void clicked(MouseButtons mouseButtons) {
		isActive = true;
	}

	public void setActive(boolean active) {
		isActive = active;
	}

	/**
	 * Return a version of the string which fits into the button,
	 * trimming it in case it should be too long
	 * 
	 * @param label Label text
	 * @return Adapted string
	 */
	public String trimToFitButton(String label) {
		if (label.length() > MAX_LABEL_LENGTH) {
			return label.substring(0, MAX_LABEL_LENGTH - 2) + "...";
		}
		else {
			return label;
		}
	}
}
