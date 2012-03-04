package com.mojang.mojam.level.gamemode;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.entity.Entity;
import com.mojang.mojam.entity.building.ShopItemBomb;
import com.mojang.mojam.entity.building.ShopItemHarvester;
import com.mojang.mojam.entity.building.ShopItemTurret;
import com.mojang.mojam.entity.mob.Team;
import com.mojang.mojam.level.DifficultyInformation;
import com.mojang.mojam.level.Level;
import com.mojang.mojam.level.LevelInformation;
import com.mojang.mojam.level.LevelUtils;
import com.mojang.mojam.level.tile.FloorTile;
import com.mojang.mojam.level.tile.SandTile;
import com.mojang.mojam.level.tile.Tile;
import com.mojang.mojam.level.tile.UnbreakableRailTile;

public class GameMode {

	public static final int LEVEL_BORDER_SIZE = 16;
	
	protected Level newLevel;
	
	public Level generateLevel(LevelInformation li)  throws IOException {
		BufferedImage bufferedImage;
		//System.out.println("Loading level from file: "+li.getPath());
		if(li.vanilla){
			bufferedImage = ImageIO.read(MojamComponent.class.getResource(li.getPath()));
		} else {
			bufferedImage = ImageIO.read(new File(li.getPath()));
		}
		int w = bufferedImage.getWidth() + LEVEL_BORDER_SIZE;
		int h = bufferedImage.getHeight() + LEVEL_BORDER_SIZE;
		
		newLevel = new Level(w, h);
		
		processLevelImage(bufferedImage, w, h);
		darkenMap(w, h);
		
		setupPlayerSpawnArea();
		setTickItems();
		setVictoryCondition();
		setTargetScore();
		return newLevel;
	}
	
	private void processLevelImage(BufferedImage bufferedImage, int w, int h) {		
		int[] rgbs = defaultRgbArray(w, h);
		
		bufferedImage.getRGB(0, 0, w - 16, h - 16, rgbs, 8 + 8 * w, w);
		
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				int col = rgbs[x + y * w] & 0xffffffff;
				loadColorTile(col, x, y);
			}
		}
	}
	
	private int[] defaultRgbArray(int width, int height) {
		int[] rgbs = new int[width * height];
		Arrays.fill(rgbs, 0xffA8A800);

		for (int y = 0 + 4; y < height - 4; y++) {
			for (int x = (width / 2) - 4; x < (width / 2) + 3; x++) {
				rgbs[x + y * width] = 0xff888800;
			}
		}
		for (int y = 0 + 5; y < height - 5; y++) {
			for (int x = (width / 2) - 2; x < (width / 2) + 1; x++) {
				rgbs[x + y * width] = 0xffA8A800;
			}
		}
		return rgbs;
	}
	
	private void darkenMap(int w, int h) {
		for (int y = 0; y < h + 1; y++) {
			for (int x = 0; x < w + 1; x++) {
				if (x <= 8 || y <= 8 || x >= w - 8 || y >= h - 8) {
					newLevel.getSeen()[x + y * (w + 1)] = true;
				}
			}
		}
	}
	
	protected void loadColorTile(int color, int x, int y) {
		
		Tile tile = LevelUtils.getNewTileFromColor(color);
		newLevel.setTile(x, y, tile);
		
		if(tile instanceof FloorTile) {
			Entity entity = LevelUtils.getNewEntityFromColor(color,x,y);
			if(entity != null) {
				newLevel.addEntity(entity);
			}
		}

	}
	

	


	protected void setupPlayerSpawnArea() {
		newLevel.maxMonsters = 1500 + (int)DifficultyInformation.calculateStrength(500);	
		
		newLevel.addEntity(new ShopItemTurret(32 * (newLevel.width / 2 - 1.5), 4.5 * 32, Team.Team2));
		newLevel.addEntity(new ShopItemHarvester(32 * (newLevel.width / 2 - .5), 4.5 * 32, Team.Team2));
		newLevel.addEntity(new ShopItemBomb(32 * (newLevel.width / 2 + .5), 4.5 * 32, Team.Team2));
		
		newLevel.addEntity(new ShopItemTurret(32 * (newLevel.width / 2 - 1.5), (newLevel.height - 4.5) * 32, Team.Team1));
		newLevel.addEntity(new ShopItemHarvester(32 * (newLevel.width / 2 - .5), (newLevel.height - 4.5) * 32, Team.Team1));
		newLevel.addEntity(new ShopItemBomb(32 * (newLevel.width / 2 + .5), (newLevel.height - 4.5) * 32, Team.Team1));
		
		for (int i=0; i<3; i++){
		    newLevel.setTile((newLevel.width / 2) - i, 7, new UnbreakableRailTile(new SandTile()));	
		    newLevel.setTile((newLevel.width / 2) - i, newLevel.height - 8, new UnbreakableRailTile(new SandTile()));
		}
	}
	
	protected void setTickItems() {
	}
	
	protected void setVictoryCondition() {
	}
	
	protected void setTargetScore() {
	}
}
