package com.mojang.mojam.level.gamemode;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.entity.building.ShopItem;
import com.mojang.mojam.entity.mob.Team;
import com.mojang.mojam.level.DifficultyInformation;
import com.mojang.mojam.level.HoleTile;
import com.mojang.mojam.level.Level;
import com.mojang.mojam.level.LevelInformation;
import com.mojang.mojam.level.tile.DestroyableWallTile;
import com.mojang.mojam.level.tile.FloorTile;
import com.mojang.mojam.level.tile.SandTile;
import com.mojang.mojam.level.tile.UnbreakableRailTile;
import com.mojang.mojam.level.tile.UnpassableSandTile;
import com.mojang.mojam.level.tile.WallTile;

public class GameMode {

	public static final int LEVEL_BORDER_SIZE = 16;
	
	protected Level newLevel;
	int localTeam;
	
	public Level generateLevel(LevelInformation li, int localTeam)  throws IOException {
		this.localTeam = localTeam;
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
				int col = rgbs[x + y * w] & 0xffffff;
				loadColorTile(col, x, y);
			}
		}
	}
	
	private int[] defaultRgbArray(int width, int height) {
		int[] rgbs = new int[width * height];
		Arrays.fill(rgbs, 0xffA8A800);

		for (int y = 0 + 4; y < height - 4; y++) {
			for (int x = 31 - 3; x < 32 + 3; x++) {
				rgbs[x + y * width] = 0xff888800;
			}
		}
		for (int y = 0 + 5; y < height - 5; y++) {
			for (int x = 31 - 1; x < 32 + 1; x++) {
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
		switch (color) {
		case 0xA8A800:
			newLevel.setTile(x, y, new SandTile());
			break;			
		case 0x969696:
			newLevel.setTile(x, y, new UnbreakableRailTile(new FloorTile()));
			break;			
		case 0x888800:
			newLevel.setTile(x, y, new UnpassableSandTile());
			break;
		case 0xFF7777:
			newLevel.setTile(x, y, new DestroyableWallTile());
			break;
		case 0x000000:
			newLevel.setTile(x, y, new HoleTile());
			break;
		case 0xff0000:
			newLevel.setTile(x, y, new WallTile());
			break;
			
		default:
			newLevel.setTile(x, y, new FloorTile());
			break;
		}
	}
	
	protected void setupPlayerSpawnArea() {
		newLevel.maxMonsters = 1500 + (int)DifficultyInformation.calculateStrength(500);	
		
		newLevel.addEntity(new ShopItem(32 * (newLevel.width / 2 - 1.5), 4.5 * 32,
				ShopItem.SHOP_TURRET, Team.Team2,localTeam));
		newLevel.addEntity(new ShopItem(32 * (newLevel.width / 2 - .5), 4.5 * 32,
				ShopItem.SHOP_HARVESTER, Team.Team2,localTeam));
		newLevel.addEntity(new ShopItem(32 * (newLevel.width / 2 + .5), 4.5 * 32,
				ShopItem.SHOP_BOMB, Team.Team2,localTeam));

		newLevel.addEntity(new ShopItem(32 * (newLevel.width / 2 - 1.5), (newLevel.height - 4.5) * 32,
				ShopItem.SHOP_TURRET, Team.Team1,localTeam));
		newLevel.addEntity(new ShopItem(32 * (newLevel.width / 2 - .5), (newLevel.height - 4.5) * 32,
				ShopItem.SHOP_HARVESTER, Team.Team1,localTeam));
		newLevel.addEntity(new ShopItem(32 * (newLevel.width / 2 + .5), (newLevel.height - 4.5) * 32,
				ShopItem.SHOP_BOMB, Team.Team1,localTeam));
		
		newLevel.setTile(31, 7, new UnbreakableRailTile(new SandTile()));
		newLevel.setTile(31, 63 - 7, new UnbreakableRailTile(new SandTile()));
	}
	
	protected void setTickItems() {
	}
	
	protected void setVictoryCondition() {
	}
	
	protected void setTargetScore() {
	}
}
