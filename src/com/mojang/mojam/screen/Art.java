package com.mojang.mojam.screen;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import com.mojang.mojam.MojamComponent;

/**
 * Art management class
 */
public class Art {
	
	public static final int NO_OPPONENT = -1;
	public static final int LORD_LARD = 0;
	public static final int HERR_VON_SPECK = 1;
	public static final int DUCHESS_DONUT = 2;
	public static final int COUNTESS_CRULLER = 3;
	public static final int NUM_CHARACTERS = 4;
	
	public static Bitmap[][] floorTiles = cut("/art/map/floortiles.png", 32, 32);
	public static Bitmap shadow_north = load("/art/shadows/shadow_north.png");
	public static Bitmap shadow_north_east = load("/art/shadows/shadow_north_east.png");
	public static Bitmap shadow_north_west = load("/art/shadows/shadow_north_west.png");
    public static Bitmap shadow_east = load("/art/shadows/shadow_east.png");
	public static Bitmap shadow_west = load("/art/shadows/shadow_west.png");
	public static int[][] floorTileColors = getColors(floorTiles);
	public static Bitmap[][] wallTiles = cut("/art/map/walltiles.png", 32, 56, 0, 0);
	public static int[][] wallTileColors = getColors(wallTiles);
	public static Bitmap[][] treasureTiles = cut("/art/map/treasure.png", 32, 56);
	public static int treasureTileColor = getColor(treasureTiles[0][0]);
	public static Bitmap[][] mobSpawner = cut("/art/map/spawner.png", 32, 40);
	public static Bitmap mobSpawnerShadow = load("/art/shadows/shadow_spawner.png");
	public static Bitmap[][] darkness = cut("/art/map/dark.png", 32, 32);
	public static Bitmap[][] mapIcons = cut("/art/map/mapicons.png", 5, 5);
	public static Bitmap shadow = load("/art/shadows/shadow_coin.png");
	public static Bitmap[][] rails = cut("/art/map/rails.png", 32, 38);
    public static Bitmap[][] spikes = cut("/art/map/spiketrap.png", 32, 32);

    // Player sheets
	private static Bitmap[][] lordLard = cut("/art/player/lord_lard_sheet.png", 32, 32);
	private static Bitmap[][] herrSpeck = cut("/art/player/herr_von_speck_sheet.png", 32, 32);
	private static Bitmap[][] duchessDonut = cut("/art/player/duchess_donut_sheet.png", 32, 32);
	private static Bitmap[][] countessCruller = cut("/art/player/countess_cruller_sheet.png", 32, 32);
	
	public static Bitmap[][] getPlayer(int characterID) {
		switch (characterID) {
		case NO_OPPONENT:
			return null;
		case LORD_LARD:
			return lordLard;
		case HERR_VON_SPECK:
			return herrSpeck;
		case DUCHESS_DONUT:
			return duchessDonut;
		case COUNTESS_CRULLER:
			return countessCruller;
		default:
			return lordLard;
		}
	}
	
	public static Bitmap[][] getLocalPlayerArt() {
		return getPlayer(MojamComponent.instance.playerCharacter);
	}
	
    public static Bitmap exclamation_mark = load ("/art/effects/exclamation_mark.png");
	
	// Player starting points
	private static Bitmap[][] startLordLard = cut("/art/player/start_lordlard.png", 32, 32);
	private static Bitmap[][] startHerrSpeck = cut("/art/player/start_herrspeck.png", 32, 32);
	private static Bitmap[][] startDuchessDonut = startLordLard;
	private static Bitmap[][] startCountessCruller = cut("/art/player/start_cruller.png", 32, 32);
	private static Bitmap[][] startNoOpponent = cut("/art/player/start_no_opponent.png", 32, 32);
	
	public static Bitmap[][] getPlayerBase(int characterID) {
		switch (characterID) {
		case NO_OPPONENT:
			return startNoOpponent;
		case LORD_LARD:
			return startLordLard;
		case HERR_VON_SPECK:
			return startHerrSpeck;
		case DUCHESS_DONUT:
			return startDuchessDonut;
		case COUNTESS_CRULLER:
			return startCountessCruller;
		default:
			return startLordLard;
		}
	}
	
	// Tooltips
	public static Bitmap tooltipBackground = load("/art/screen/tooltipBackground.png");
    public static Bitmap turretText = load("/art/screen/atlasTurretText.png");
    public static Bitmap harvesterText = load("/art/screen/atlasHarvesterText.png");
    public static Bitmap bombText = load("/art/screen/atlasBombText.png");

	// Screens
	public static Bitmap titleScreen = load("/art/screen/TITLESCREEN.png");
	public static Bitmap howToPlayScreen = load("/art/screen/how_to_play.png");
	public static Bitmap emptyBackground = load("/art/screen/empty_background.png");
	public static Bitmap gameOverScreen = load("/art/screen/game_over.png");
	public static Bitmap pauseScreen = load("/art/screen/pause_screen.png");
	public static Bitmap mojangLogo = load("/art/logo/mojang.png");
	
	// UI elements
	public static Bitmap[][] button = cut("/art/screen/button.png", 128, 24);
    public static Bitmap[][] checkbox = cut("/art/screen/checkbox.png", 24, 24);
	public static Bitmap panel = load("/art/screen/panel/panel.png");
	public static Bitmap[][] panel_healthBar = cut("/art/screen/panel/panel_healthbar.png", 100, 6);
	public static Bitmap panel_heart = load("/art/screen/panel/p_heart.png");
	public static Bitmap panel_coin = load("/art/screen/panel/p_coin.png");
	public static Bitmap panel_star = load("/art/screen/panel/p_level.png");
    public static Bitmap[][] panel_xpBar = cut("/art/screen/panel/panel_xpbar.png", 100, 6);
	public static Bitmap background = load("/art/screen/BACKGROUND.png");
    public static Bitmap[][] slider = cut("/art/screen/slider.png", 16, 24);
	
    // Buildings
	public static Bitmap[][] harvester = cut("/art/building/bot_vacuum.png", 32, 56);
	public static Bitmap[][] harvester2 = cut("/art/building/bot_vacuum2.png", 32, 56);
	public static Bitmap[][] harvester3 = cut("/art/building/bot_vacuum3.png", 32, 56);
	public static Bitmap[][] turret = cut("/art/building/turret.png", 32, 32);
	public static Bitmap[][] turret2 = cut("/art/building/turret2.png", 32, 32);
	public static Bitmap[][] turret3 = cut("/art/building/turret3.png", 32, 32);
	public static Bitmap bomb = load("/art/building/bomb.png");

	// Fonts
	public static Bitmap[][] font_default = cut("/art/fonts/font_default.png", 8, 8);
	public static Bitmap[][] font_blue = cut("/art/fonts/font_blue.png", 8, 8);
	public static Bitmap[][] font_gray = cut("/art/fonts/font_gray.png", 8, 8);
	public static Bitmap[][] font_red = cut("/art/fonts/font_red.png", 8, 8);
	public static Bitmap[][] font_gold = cut("/art/fonts/font_gold.png", 8, 8);
	
	public static Bitmap[][] font_small_black = cutv("/art/fonts/font_small_black.png", 7);
    public static Bitmap[][] font_small_white = cutv("/art/fonts/font_small_white.png", 7);
    public static Bitmap[][] font_small_gold = cutv("/art/fonts/font_small_gold.png", 7);

	// Mob
    public static Bitmap[][] raildroid = cut("/art/mob/raildroid.png", 32, 32);
	public static Bitmap[][] mummy = cut("/art/mob/enemy_mummy_anim_48.png", 48, 48);
	public static Bitmap[][] snake = cut("/art/mob/enemy_snake_anim_48.png", 48, 48);
	public static Bitmap[][] scarab = cut("/art/mob/enemy_scarab_anim_48.png", 48, 48);
	public static Bitmap[][] bat = cut("/art/mob/enemy_bat_32.png", 32, 32);
	public static Bitmap batShadow = load("/art/shadows/shadow_bat.png");

	// Coins
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

	// Bullets and special effects
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
	public static Bitmap[][] moneyBar = cut("/art/effects/bar_blue.png", 32, 4);
	public static Bitmap[][] healthBar = cut("/art/effects/bar_green.png", 32, 4);
	public static Bitmap[][] healthBar_Outline = cut("/art/effects/bar_outline.png", 32, 4);
	public static Bitmap[][] healthBar_Underlay = cut("/art/effects/bar_green_underlay.png", 32, 4);
		
	// Icons
	public static BufferedImage icon32 = loadBufferedImage("/art/icon/icon32.png");
	public static BufferedImage icon64 = loadBufferedImage("/art/icon/icon64.png");
	
    /**
     * Return the bitmaps for a given piece of art, cut out from a sheet
     * 
     * @param string Art piece name
     * @param w Width of a single bitmap
     * @param h Height of a single bitmap
     * @return Bitmap array
     */
	public static Bitmap[][] cut(String string, int w, int h) {
	    return cut(string, w, h, 0, 0);
	}

    /**
     * Return the bitmaps for a given piece of art, cut out from a sheet
     * 
     * @param string Art piece name
     * @param w Width of a single bitmap
     * @param h Height of a single bitmap
     * @param bx
     * @param by
     * @return Bitmap array
     */
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

    private static Bitmap[][] cutv(String string, int h) {
        try {
            BufferedImage bi = ImageIO.read(MojamComponent.class.getResource(string));

            int yTiles = bi.getHeight() / h;

            int xTiles = 0;
            Bitmap[][] result = new Bitmap[yTiles][];
            for (int y = 0; y < yTiles; y++) {
                List<Bitmap> row = new ArrayList<Bitmap>();
                int xCursor=0;
                while (xCursor < bi.getWidth()) {
                    int w = 0;
                    while (xCursor + w < bi.getWidth() && bi.getRGB(xCursor + w, y * h) != 0xffed1c24) {
                        w++;
                    }
                    if (w > 0) {
                        Bitmap bitmap = new Bitmap(w, h);
                        bi.getRGB(xCursor, y * h, w, h, bitmap.pixels, 0, w );
                        row.add(bitmap);
                    }
                    xCursor += w+1;
                }
                if (xTiles < row.size()) xTiles = row.size();
                result[y] = row.toArray(new Bitmap[0]);
            }

            Bitmap[][] resultT = new Bitmap[xTiles][yTiles];
            for (int x = 0; x < xTiles; x++) {
                for (int y = 0; y < yTiles; y++) {
                    try {
                        resultT[x][y] = result[y][x];
                    } catch (IndexOutOfBoundsException e) {
                        resultT[x][y] = null;
                    }
                }
            }

            return resultT;
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

	/**
	 * Load a bitmap resource by name
	 * 
	 * @param string Resource name
	 * @return Bitmap on success, null on error
	 */
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
	
	/**
	 * Load a bitmap resource by name
	 * 
	 * @param string Resource name
	 * @return BufferedImage on success, null on error
	 */
	private static BufferedImage loadBufferedImage(String string) {
		try {
			BufferedImage bi = ImageIO.read(MojamComponent.class
					.getResource(string));
			return bi;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
}