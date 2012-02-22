package com.mojang.mojam.screen;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.mojang.mojam.MojamComponent;

public class Art {
	public static Bitmap[][] floorTiles = cut("/art/map/floortiles.png", 32, 32);
	public static int[][] floorTileColors = getColors(floorTiles);
	public static Bitmap[][] wallTiles = cut("/art/map/floortiles.png", 32, 56, 0, 104);
	public static int[][] wallTileColors = getColors(wallTiles);
	public static Bitmap[][] treasureTiles = cut("/art/map/treasure.png", 32, 56);
	public static Bitmap[][] mobSpawner = cut("/art/map/spawner.png", 32, 40);
	public static Bitmap[][] darkness = cut("/art/map/dark.png", 32, 32);
	public static Bitmap[][] mapIcons = cut("/art/map/mapicons.png", 5, 5);
	public static Bitmap shadow = load("/art/map/shadow.png");
      	public static Bitmap[][] rails = cut("/art/map/rails.png", 32, 38);

	public static Bitmap[][] lordLard = cut("/art/player/lord_lard_sheet.png", 32, 32);
	public static Bitmap[][] herrSpeck = cut("/art/player/herr_von_speck_sheet.png", 32, 32);
	public static Bitmap[][] startLordLard = cut("/art/player/start_lordlard.png", 32, 32);
	public static Bitmap[][] startHerrSpeck = cut("/art/player/start_herrspeck.png", 32, 32);
        
	public static Bitmap titleScreen = load("/art/screen/TITLESCREEN.png");
	public static Bitmap howToPlayScreen = load("/art/screen/how_to_play.png");
	public static Bitmap emptyBackground = load("/art/screen/empty_background.png");
	public static Bitmap gameOverScreen = load("/art/screen/game_over.png");
	public static Bitmap pauseScreen = load("/art/screen/pause_screen.png");
	public static Bitmap[][] button = cut("/art/screen/button.png", 128, 24);
	public static Bitmap panel = load("/art/screen/panel.png");
        public static Bitmap background = load("/art/screen/BACKGROUND.png");
        
	public static Bitmap[][] harvester = cut("/art/building/bot_vacuum.png", 32, 56);
	public static Bitmap[][] harvester2 = cut("/art/building/bot_vacuum2.png", 32, 56);
	public static Bitmap[][] harvester3 = cut("/art/building/bot_vacuum3.png", 32, 56);
	public static Bitmap[][] turret = cut("/art/building/turret.png", 32, 32);
	public static Bitmap[][] turret2 = cut("/art/building/turret2.png", 32, 32);
	public static Bitmap[][] turret3 = cut("/art/building/turret3.png", 32, 32);
	public static Bitmap bomb = load("/art/building/bomb.png");
        
	public static Bitmap[][] font_default = cut("/art/fonts/font_default.png", 8, 8);
	public static Bitmap[][] font_blue = cut("/art/fonts/font_blue.png", 8, 8);
	public static Bitmap[][] font_gray = cut("/art/fonts/font_gray.png", 8, 8);
	public static Bitmap[][] font_red = cut("/art/fonts/font_red.png", 8, 8);
	public static Bitmap[][] font_gold = cut("/art/fonts/font_gold.png", 8, 8);

        public static Bitmap[][] raildroid = cut("/art/mob/raildroid.png", 32, 32);
	public static Bitmap[][] mummy = cut("/art/mob/enemy_mummy_anim_48.png", 48, 48);
	public static Bitmap[][] snake = cut("/art/mob/enemy_snake_anim_48.png", 48, 48);
	public static Bitmap[][] bat = cut("/art/mob/enemy_bat_32.png", 32, 32);
	public static Bitmap batShadow = load("/art/mob/shadow.png");

	public static Bitmap[][] pickupCoinBronzeSmall = cut("/art/pickup/pickup_coin_bronze_small_8.png", 8, 8);
	public static Bitmap[][] pickupCoinBronze = cut("/art/pickup/pickup_coin_bronze_16.png", 16, 16);
	public static Bitmap[][] pickupCoinSilverSmall = cut("/art/pickup/pickup_coin_silver_small_8.png", 8, 8);
	public static Bitmap[][] pickupCoinSilver = cut("/art/pickup/pickup_coin_silver_16.png", 16, 16);
	public static Bitmap[][] pickupCoinGoldSmall = cut("/art/pickup/pickup_coin_gold_small_8.png", 8, 8);
	public static Bitmap[][] pickupCoinGold = cut("/art/pickup/pickup_coin_gold_16.png", 16, 16);
	public static Bitmap[][] pickupGemEmerald = cut("/art/pickup/pickup_gem_emerald_12.png", 16, 16);
	public static Bitmap[][] pickupGemRuby = cut("/art/pickup/pickup_gem_ruby_12.png", 16, 16);
	public static Bitmap[][] pickupGemDiamond = cut("/art/pickup/pickup_gem_diamond_24.png", 24, 24);
	public static Bitmap[][] shineSmall = cut("/art/pickup/effect_shine_small_13.png", 13, 13);
	public static Bitmap[][] shineBig = cut("/art/pickup/effect_shine_big_13.png", 13, 13);

	public static Bitmap[][] bullets = cut("/art/effects/bullets.png", 16, 16);
	public static Bitmap[][] bullet = cut("/art/effects/bullet.png", 16, 16);
	public static Bitmap[][] muzzle = cut("/art/effects/muzzle.png", 16, 16);
	public static Bitmap[][] fxEnemyDie = cut("/art/effects/fx_enemydie_64.png", 64, 64);
	public static Bitmap[][] fxSteam24 = cut("/art/effects/fx_steam1_24.png", 24, 24);
	public static Bitmap[][] fxSteam12 = cut("/art/effects/fx_steam2_12.png", 12, 12);
	public static Bitmap[][] fxBombSplosion = cut("/art/effects/fx_bombsplosion_big_32.png", 32, 32);
	public static Bitmap[][] fxBombSplosionSmall = cut("/art/effects/fx_bombsplosion_small_32.png", 32, 32);
	public static Bitmap[][] fxDust12 = cut("/art/effects/fx_dust2_12.png", 12, 12);
	public static Bitmap[][] fxDust24 = cut("/art/effects/fx_dust1_24.png", 24, 24);

	public static Bitmap[][] cut(String string, int w, int h) {
		return cut(string, w, h, 0, 0);
	}

	private static Bitmap[][] cut(String string, int w, int h, int bx, int by) {
		try {
			BufferedImage bi = ImageIO.read(MojamComponent.class
					.getResource(string));

			int xTiles = (bi.getWidth() - bx) / w;
			int yTiles = (bi.getHeight() - by) / h;

			Bitmap[][] result = new Bitmap[xTiles][yTiles];

			for (int x = 0; x < xTiles; x++) {
				for (int y = 0; y < yTiles; y++) {
					result[x][y] = new Bitmap(w, h);
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

	private static int[][] getColors(Bitmap[][] tiles) {
		int[][] result = new int[tiles.length][tiles[0].length];
		for (int y = 0; y < tiles[0].length; y++) {
			for (int x = 0; x < tiles.length; x++) {
				result[x][y] = getColor(tiles[x][y]);
			}
		}
		return result;
	}

	private static int getColor(Bitmap bitmap) {
		int r = 0;
		int g = 0;
		int b = 0;
		for (int i = 0; i < bitmap.pixels.length; i++) {
			int col = bitmap.pixels[i];
			r += (col >> 16) & 0xff;
			g += (col >> 8) & 0xff;
			b += (col) & 0xff;
		}

		r /= bitmap.pixels.length;
		g /= bitmap.pixels.length;
		b /= bitmap.pixels.length;

		return 0xff000000 | r << 16 | g << 8 | b;
	}

	private static Bitmap load(String string) {
		try {
			BufferedImage bi = ImageIO.read(MojamComponent.class
					.getResource(string));

			int w = bi.getWidth();
			int h = bi.getHeight();

			Bitmap result = new Bitmap(w, h);
			bi.getRGB(0, 0, w, h, result.pixels, 0, w);

			return result;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	private static Bitmap[] cut(String string, int h) {
		try {
			BufferedImage bi = ImageIO.read(MojamComponent.class
					.getResource(string));

			int yTiles = bi.getHeight() / h;
			int w = bi.getWidth();

			Bitmap[] result = new Bitmap[yTiles];

			for (int y = 0; y < yTiles; y++) {
				result[y] = new Bitmap(w, h);
				bi.getRGB(0, y * h, w, h, result[y].pixels, 0, w);
			}

			return result;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
}