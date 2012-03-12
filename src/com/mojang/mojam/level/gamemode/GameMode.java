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
import com.mojang.mojam.entity.building.ShopItemRaygun;
import com.mojang.mojam.entity.building.ShopItemShotgun;
import com.mojang.mojam.entity.building.ShopItemTurret;
import com.mojang.mojam.entity.mob.Team;
import com.mojang.mojam.gui.TitleMenu;
import com.mojang.mojam.level.Level;
import com.mojang.mojam.level.LevelInformation;
import com.mojang.mojam.level.LevelUtils;
import com.mojang.mojam.level.tile.FloorTile;
import com.mojang.mojam.level.tile.PlayerSpawn;
import com.mojang.mojam.level.tile.SandTile;
import com.mojang.mojam.level.tile.Tile;
import com.mojang.mojam.level.tile.UnbreakableRailTile;
import com.mojang.mojam.level.tile.WallTile;
import com.mojang.mojam.math.Vec2;
import com.mojang.mojam.tiled.TiledMap;

public class GameMode {

	public static final int LEVEL_BORDER_SIZE = 16;
	
	protected Level newLevel;
	private TiledMap map;
	
	public Level generateLevel(LevelInformation li)  throws IOException {
		BufferedImage bufferedImage;
		int w;
		int h;
		int layers;
		
		try {
			map = new TiledMap(MojamComponent.class.getResourceAsStream(li.getPath()));
			layers = map.getLayerCount();
			if(layers < 3) {
				throw new IOException();
			}
			w = map.getWidth();
			h = map.getHeight();
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			map = null;
			w = 0;
			h = 0;
			layers = 0;
		}
		
		newLevel = new Level(w, h);
		
		processLevelMap(w, h, layers);
		//processLevelImage(bufferedImage, w, h);
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
	
	private void processLevelMap(int w, int h, int layers) {
		for (int l = 0; l < layers; l++) {
				processMapLayer(w, h, l);
		}
	}
	
	private void processMapLayer(int w, int h, int mapLayer) {
		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				int id = this.map.getTileId(x, y, mapLayer);
				
				Object obj = LevelUtils.getNewObjectFromId(x, y, id);
				
				if (obj != null) {
					if (obj instanceof Tile) {
						newLevel.setTile(x, y, (Tile)obj);
					}
					if (obj instanceof Entity) {
						newLevel.addEntity((Entity)obj);
					}
				}
			}
		}
	}
	
	private int[] defaultRgbArray(int width, int height) {
		int[] rgbs = new int[width * height];
		// fill everything with UnpassableSandTiles
		Arrays.fill(rgbs, 0xffA8A800);
		
		// add SandTiles for player bases
		for (int y = 0 + 4; y < height - 4; y++) {
			for (int x = (width / 2) - 5; x < (width / 2) + 4; x++) {
				rgbs[x + y * width] = 0xff888800;
			}
		}
		for (int y = 0 + 5; y < height - 5; y++) {
			for (int x = (width / 2) - 3; x < (width / 2) + 2; x++) {
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
		newLevel.maxMonsters = 1500 + (int)TitleMenu.difficulty.calculateStrength(500);
		
		newLevel.addEntity(new ShopItemTurret(32 * (newLevel.width / 2 - 1.5), 4.5 * 32, Team.Team2));
		newLevel.addEntity(new ShopItemHarvester(32 * (newLevel.width / 2 - .5), 4.5 * 32, Team.Team2));
		newLevel.addEntity(new ShopItemBomb(32 * (newLevel.width / 2 + .5), 4.5 * 32, Team.Team2));
		newLevel.addEntity(new ShopItemShotgun(32 * (newLevel.width / 2 - 2.5), 6.5 * 32, Team.Team2));
		newLevel.addEntity(new ShopItemRaygun(32 * (newLevel.width / 2 - 2.5), 5.5 * 32, Team.Team2));
		
		newLevel.addEntity(new ShopItemTurret(32 * (newLevel.width / 2 - 1.5), (newLevel.height - 4.5) * 32, Team.Team1));
		newLevel.addEntity(new ShopItemHarvester(32 * (newLevel.width / 2 - .5), (newLevel.height - 4.5) * 32, Team.Team1));
		newLevel.addEntity(new ShopItemBomb(32 * (newLevel.width / 2 + .5), (newLevel.height - 4.5) * 32, Team.Team1));
		newLevel.addEntity(new ShopItemShotgun(32 * (newLevel.width / 2 - 2.5), (newLevel.height - 6.5) * 32, Team.Team1));
		newLevel.addEntity(new ShopItemRaygun(32 * (newLevel.width / 2 - 2.5), (newLevel.height - 5.5) * 32, Team.Team1));

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