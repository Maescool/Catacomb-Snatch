package com.mojang.mojam.screen;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.mojang.mojam.GameCharacter;
import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.gui.CharacterButton;
import com.mojang.mojam.gui.LevelButton;
import com.mojang.mojam.gui.LevelEditorButton;
import com.mojang.mojam.gui.components.Button;

/**
 * Art management class
 */
public class Art {

	public static AbstractBitmap[][] floorTiles;
	public static AbstractBitmap shadow_north;
	public static AbstractBitmap shadow_north_east;
	public static AbstractBitmap shadow_north_west;
	public static AbstractBitmap shadow_east;
	public static AbstractBitmap shadow_west;
	public static int[][] floorTileColors;
	public static AbstractBitmap[][] wallTiles;
	public static int[][] wallTileColors;
	public static AbstractBitmap[][] treasureTiles;
	public static int treasureTileColor;
	public static AbstractBitmap[][] mobSpawner;
	public static AbstractBitmap mobSpawnerShadow;
	public static AbstractBitmap[][] darkness;
	public static AbstractBitmap[][] mapIcons;
	public static AbstractBitmap shadow;
	public static AbstractBitmap[][] rails;
	public static AbstractBitmap[][] spikes;
	public static AbstractBitmap[][] dropFloor;
	// Player sheets
	private static AbstractBitmap[][] lordLard;
	private static AbstractBitmap[][] herrSpeck;
	private static AbstractBitmap[][] duchessDonut;
	private static AbstractBitmap[][] countessCruller;

	public static AbstractBitmap[][] getPlayer(GameCharacter character) {
		switch (character) {
			case None:
				return null;
			case LordLard:
				return lordLard;
			case HerrVonSpeck:
				return herrSpeck;
			case DuchessDonut:
				return duchessDonut;
			case CountessCruller:
				return countessCruller;
			default:
				return lordLard;
		}
	}

	public static AbstractBitmap[][] getLocalPlayerArt() {
		return getPlayer(MojamComponent.instance.playerCharacter);
	}
	public static AbstractBitmap exclamation_mark;
	// Player starting points
	private static AbstractBitmap[][] startLordLard;
	private static AbstractBitmap[][] startHerrSpeck;
	private static AbstractBitmap[][] startDuchessDonut;
	private static AbstractBitmap[][] startCountessCruller;
	private static AbstractBitmap[][] startNoOpponent;

	public static AbstractBitmap[][] getPlayerSpawn(GameCharacter character) {
		switch (character) {
			case None:
				return startNoOpponent;
			case LordLard:
				return startLordLard;
			case HerrVonSpeck:
				return startHerrSpeck;
			case DuchessDonut:
				return startDuchessDonut;
			case CountessCruller:
				return startCountessCruller;
			default:
				return startLordLard;
		}
	}
	// Player base, left
	private static AbstractBitmap[][] startLordLardLeft;
	private static AbstractBitmap[][] startHerrSpeckLeft;
	private static AbstractBitmap[][] startDuchessDonutLeft;
	private static AbstractBitmap[][] startCountessCrullerLeft;
	private static AbstractBitmap[][] startNoOpponentLeft;

	public static AbstractBitmap[][] getPlayerBaseLeft(GameCharacter character) {
		switch (character) {
			case None:
				return startNoOpponentLeft;
			case LordLard:
				return startLordLardLeft;
			case HerrVonSpeck:
				return startHerrSpeckLeft;
			case DuchessDonut:
				return startDuchessDonutLeft;
			case CountessCruller:
				return startCountessCrullerLeft;
			default:
				return startLordLardLeft;
		}
	}
	// Player base, right
	private static AbstractBitmap[][] startLordLardRight;
	private static AbstractBitmap[][] startHerrSpeckRight;
	private static AbstractBitmap[][] startDuchessDonutRight;
	private static AbstractBitmap[][] startCountessCrullerRight;
	private static AbstractBitmap[][] startNoOpponentRight;

	public static AbstractBitmap[][] getPlayerBaseRight(GameCharacter character) {
		switch (character) {
			case None:
				return startNoOpponentRight;
			case LordLard:
				return startLordLardRight;
			case HerrVonSpeck:
				return startHerrSpeckRight;
			case DuchessDonut:
				return startDuchessDonutRight;
			case CountessCruller:
				return startCountessCrullerRight;
			default:
				return startLordLardRight;
		}
	}
	// Tooltips
	public static AbstractBitmap tooltipBackground;
	public static AbstractBitmap turretText;
	public static AbstractBitmap harvesterText;
	public static AbstractBitmap bombText;
	// Screens
	public static AbstractBitmap titleScreen;
	public static AbstractBitmap howToPlayScreen;
	public static AbstractBitmap emptyBackground;
	public static AbstractBitmap gameOverScreen;
	public static AbstractBitmap pauseScreen;
	public static AbstractBitmap mojangLogo;
	public static AbstractBitmap downloadScreen;
	// UI elements
	public static AbstractBitmap[][] button;
	public static AbstractBitmap[][] checkbox;
	public static AbstractBitmap panel;
	public static AbstractBitmap[][] panel_healthBar;
	public static AbstractBitmap panel_heart;
	public static AbstractBitmap panel_coin;
	public static AbstractBitmap panel_star;
	public static AbstractBitmap[][] panel_xpBar;
	public static AbstractBitmap background;
	public static AbstractBitmap[][] slider;
	// Buildings
	public static AbstractBitmap[][] harvester;
	public static AbstractBitmap[][] harvester2;
	public static AbstractBitmap[][] harvester3;
	public static AbstractBitmap[][] turret;
	public static AbstractBitmap[][] turret2;
	public static AbstractBitmap[][] turret3;
	public static AbstractBitmap bomb;
	public static AbstractBitmap[][] small_chest;
	public static AbstractBitmap[][] large_chest;
	public static AbstractBitmap[][] teamTurret1;
	public static AbstractBitmap[][] teamTurret2;
	//Weapons
	public static AbstractBitmap[][] weaponList;
	// Fonts
	public static AbstractBitmap[][] font_default;
	public static AbstractBitmap[][] font_blue;
	public static AbstractBitmap[][] font_gray;
	public static AbstractBitmap[][] font_red;
	public static AbstractBitmap[][] font_gold;
	public static AbstractBitmap[][] font_small_black;
	public static AbstractBitmap[][] font_small_white;
	public static AbstractBitmap[][] font_small_gold;
	// Mob
	public static AbstractBitmap[][] raildroid;
	public static AbstractBitmap[][] mummy;
	public static AbstractBitmap[][] pharao;
	public static AbstractBitmap[][] snake;
	public static AbstractBitmap[][] scarab;
	public static AbstractBitmap[][] bat;
	public static AbstractBitmap batShadow;
	// Coins
	public static AbstractBitmap[][] pickupCoinBronzeSmall;
	public static AbstractBitmap[][] pickupCoinBronze;
	public static AbstractBitmap[][] pickupCoinSilverSmall;
	public static AbstractBitmap[][] pickupCoinSilver;
	public static AbstractBitmap[][] pickupCoinGoldSmall;
	public static AbstractBitmap[][] pickupCoinGold;
	public static AbstractBitmap[][] pickupGemEmerald;
	public static AbstractBitmap[][] pickupGemRuby;
	public static AbstractBitmap[][] pickupGemDiamond;
	public static AbstractBitmap[][] shineSmall;
	public static AbstractBitmap[][] shineBig;

	// Bullets and special effects
	public static AbstractBitmap[][] bullets;
	public static AbstractBitmap[][] bullet;
	public static AbstractBitmap buckShot;
	public static AbstractBitmap[][] bulletflame;
	public static AbstractBitmap[][] plasmaBall;
	public static AbstractBitmap[][] bulletpoison;
	public static AbstractBitmap[][] muzzle;
	public static AbstractBitmap[][] fxEnemyDie;
	public static AbstractBitmap[][] fxSteam24;
	public static AbstractBitmap[][] fxSteam12;
	public static AbstractBitmap[][] fxBombSplosion;
	public static AbstractBitmap[][] fxBombSplosionSmall;
	public static AbstractBitmap[][] fxDust12;
	public static AbstractBitmap[][] fxDust24;
	public static AbstractBitmap[][] moneyBar;
	public static AbstractBitmap[][] healthBar;
	public static AbstractBitmap[][] healthBar_Outline;
	public static AbstractBitmap[][] healthBar_Underlay;
	public static AbstractBitmap[][] sprintBar;
	public static AbstractBitmap[][] dish;
	
	// Backgrounds
	public static AbstractBitmap backCharacterButton[];
	public static AbstractBitmap backLevelEditorButton[];
	public static AbstractBitmap backLevelButton[];

	// Icons
	public static BufferedImage icon32 = loadBufferedImage("/art/icon/icon32.png");
	public static BufferedImage icon64 = loadBufferedImage("/art/icon/icon64.png");

	public static void loadAllResources(AbstractScreen screen) {
		floorTiles = screen.cut("/art/map/floortiles.png", 32, 32);
		shadow_north = screen.load("/art/shadows/shadow_north.png");
		shadow_north_east = screen.load("/art/shadows/shadow_north_east.png");
		shadow_north_west = screen.load("/art/shadows/shadow_north_west.png");
		shadow_east = screen.load("/art/shadows/shadow_east.png");
		shadow_west = screen.load("/art/shadows/shadow_west.png");
		floorTileColors = screen.getColors(floorTiles);
		wallTiles = screen.cut("/art/map/walltiles.png", 32, 56, 0, 0);
		wallTileColors = screen.getColors(wallTiles);
		treasureTiles = screen.cut("/art/map/treasure.png", 32, 56);
		treasureTileColor = screen.getColor(treasureTiles[0][0]);
		mobSpawner = screen.cut("/art/map/spawner.png", 32, 40);
		mobSpawnerShadow = screen.load("/art/shadows/shadow_spawner.png");
		darkness = screen.cut("/art/map/dark.png", 32, 32);
		mapIcons = screen.cut("/art/map/mapicons.png", 5, 5);
		shadow = screen.load("/art/shadows/shadow_coin.png");
		rails = screen.cut("/art/map/rails.png", 32, 38);
		spikes = screen.cut("/art/map/spiketrap.png", 32, 32);
		dropFloor = screen.cut("/art/map/droptrap.png", 32, 32);
		// Player sheets
		lordLard = screen.cut("/art/player/lord_lard_sheet.png", 32, 32);
		herrSpeck = screen.cut("/art/player/herr_von_speck_sheet.png", 32, 32);
		duchessDonut = screen.cut("/art/player/duchess_donut_sheet.png", 32, 32);
		countessCruller = screen.cut("/art/player/countess_cruller_sheet.png", 32, 32);
		exclamation_mark = screen.load("/art/effects/exclamation_mark.png");
		// Player starting points
		startLordLard = screen.cut("/art/player/start_lordlard.png", 32, 32);
		startHerrSpeck = screen.cut("/art/player/start_herrspeck.png", 32, 32);
		startDuchessDonut = screen.cut("/art/player/start_donut.png", 32, 32);
		startCountessCruller = screen.cut("/art/player/start_cruller.png", 32, 32);
		startNoOpponent = screen.cut("/art/player/start_no_opponent.png", 32, 32);
		// Player base, left
		startLordLardLeft = screen.cut("/art/player/start_lordlard_left.png", 32, 32);
		startHerrSpeckLeft = screen.cut("/art/player/start_herrspeck_left.png", 32, 32);
		startDuchessDonutLeft = screen.cut("/art/player/start_donut_left.png", 32, 32);
		startCountessCrullerLeft = screen.cut("/art/player/start_cruller_left.png", 32, 32);
		startNoOpponentLeft = screen.cut("/art/player/start_no_opponent_left.png", 32, 32);
		// Player base, right
		startLordLardRight = screen.cut("/art/player/start_lordlard_right.png", 32, 32);
		startHerrSpeckRight = screen.cut("/art/player/start_herrspeck_right.png", 32, 32);
		startDuchessDonutRight = screen.cut("/art/player/start_donut_right.png", 32, 32);
		startCountessCrullerRight = screen.cut("/art/player/start_cruller_right.png", 32, 32);
		startNoOpponentRight = screen.cut("/art/player/start_no_opponent_right.png", 32, 32);
		// Tooltips
		tooltipBackground = screen.load("/art/screen/tooltipBackground.png");
		turretText = screen.load("/art/screen/atlasTurretText.png");
		harvesterText = screen.load("/art/screen/atlasHarvesterText.png");
		bombText = screen.load("/art/screen/atlasBombText.png");
		// Screens
		titleScreen = screen.load("/art/screen/TITLESCREEN.png");
		howToPlayScreen = screen.load("/art/screen/how_to_play.png");
		emptyBackground = screen.load("/art/screen/empty_background.png");
		gameOverScreen = screen.load("/art/screen/game_over.png");
		pauseScreen = screen.load("/art/screen/pause_screen.png");
		mojangLogo = screen.load("/art/logo/mojang.png");
		downloadScreen = screen.load("/art/screen/download_screen.png");
		// UI elements
		button = screen.cut("/art/screen/button.png", 128, 24);
		checkbox = screen.cut("/art/screen/checkbox.png", 24, 24);
		panel = screen.load("/art/screen/panel/panel.png");
		panel_healthBar = screen.cut("/art/screen/panel/panel_healthbar.png", 100, 6);
		panel_heart = screen.load("/art/screen/panel/p_heart.png");
		panel_coin = screen.load("/art/screen/panel/p_coin.png");
		panel_star = screen.load("/art/screen/panel/p_level.png");
		panel_xpBar = screen.cut("/art/screen/panel/panel_xpbar.png", 100, 6);
		background = screen.load("/art/screen/BACKGROUND.png");
		slider = screen.cut("/art/screen/slider.png", 16, 24);
		// Buildings
		harvester = screen.cut("/art/building/bot_vacuum.png", 32, 56);
		harvester2 = screen.cut("/art/building/bot_vacuum2.png", 32, 56);
		harvester3 = screen.cut("/art/building/bot_vacuum3.png", 32, 56);
		turret = screen.cut("/art/building/turret.png", 32, 32);
		turret2 = screen.cut("/art/building/turret2.png", 32, 32);
		turret3 = screen.cut("/art/building/turret3.png", 32, 32);
		bomb = screen.load("/art/building/bomb.png");
		small_chest = screen.cut("/art/building/chest_small.png", 32, 53);
		large_chest = screen.cut("/art/building/chest_large.png", 64, 53);
		teamTurret1 = screen.cut("/art/building/turretTeam1.png", 32, 32);
		teamTurret2 = screen.cut("/art/building/turretTeam2.png", 32, 32);
		//Weapons
		weaponList = screen.cut("/art/weapons/weapon_list.png", 32, 32);
		// Fonts
		font_default = screen.cut("/art/fonts/font_default.png", 8, 8);
		font_blue = screen.cut("/art/fonts/font_blue.png", 8, 8);
		font_gray = screen.cut("/art/fonts/font_gray.png", 8, 8);
		font_red = screen.cut("/art/fonts/font_red.png", 8, 8);
		font_gold = screen.cut("/art/fonts/font_gold.png", 8, 8);
		font_small_black = screen.cutv("/art/fonts/font_small_black.png", 7);
		font_small_white = screen.cutv("/art/fonts/font_small_white.png", 7);
		font_small_gold = screen.cutv("/art/fonts/font_small_gold.png", 7);
		// Mob
		raildroid = screen.cut("/art/mob/raildroid.png", 32, 32);
		mummy = screen.cut("/art/mob/enemy_mummy_anim_48.png", 48, 48);
		pharao = screen.cut("/art/mob/enemy_pharao_anim_48.png", 48, 48);
		snake = screen.cut("/art/mob/enemy_snake_anim_48.png", 48, 48);
		scarab = screen.cut("/art/mob/enemy_scarab_anim_48.png", 48, 48);
		bat = screen.cut("/art/mob/enemy_bat_32.png", 32, 32);
		batShadow = screen.load("/art/shadows/shadow_bat.png");
		// Coins
		pickupCoinBronzeSmall = screen.cut("/art/pickup/pickup_coin_bronze_small_8.png", 8, 8);
		pickupCoinBronze = screen.cut("/art/pickup/pickup_coin_bronze_16.png", 16, 16);
		pickupCoinSilverSmall = screen.cut("/art/pickup/pickup_coin_silver_small_8.png", 8, 8);
		pickupCoinSilver = screen.cut("/art/pickup/pickup_coin_silver_16.png", 16, 16);
		pickupCoinGoldSmall = screen.cut("/art/pickup/pickup_coin_gold_small_8.png", 8, 8);
		pickupCoinGold = screen.cut("/art/pickup/pickup_coin_gold_16.png", 16, 16);
		pickupGemEmerald = screen.cut("/art/pickup/pickup_gem_emerald_12.png", 16, 16);
		pickupGemRuby = screen.cut("/art/pickup/pickup_gem_ruby_12.png", 16, 16);
		pickupGemDiamond = screen.cut("/art/pickup/pickup_gem_diamond_24.png", 24, 24);
		shineSmall = screen.cut("/art/pickup/effect_shine_small_13.png", 13, 13);
		shineBig = screen.cut("/art/pickup/effect_shine_big_13.png", 13, 13);
		// Bullets and special effects
		bullets = screen.cut("/art/effects/bullets.png", 16, 16);
		bullet = screen.cut("/art/effects/bullet.png", 16, 16);
		buckShot = screen.load("/art/effects/bullet_buckshot.png");
		bulletflame = screen.cut("/art/effects/bullet_flame.png", 16, 16);
		plasmaBall = screen.cut("/art/effects/plasmaball.png", 16, 16);
		bulletpoison = screen.cut("/art/effects/bullet_poison.png", 16, 16);
		muzzle = screen.cut("/art/effects/muzzle.png", 16, 16);
		fxEnemyDie = screen.cut("/art/effects/fx_enemydie_64.png", 64, 64);
		fxSteam24 = screen.cut("/art/effects/fx_steam1_24.png", 24, 24);
		fxSteam12 = screen.cut("/art/effects/fx_steam2_12.png", 12, 12);
		fxBombSplosion = screen.cut("/art/effects/fx_bombsplosion_big_32.png", 32, 32);
		fxBombSplosionSmall = screen.cut("/art/effects/fx_bombsplosion_small_32.png", 32, 32);
		fxDust12 = screen.cut("/art/effects/fx_dust2_12.png", 12, 12);
		fxDust24 = screen.cut("/art/effects/fx_dust1_24.png", 24, 24);
		moneyBar = screen.cut("/art/effects/bar_blue.png", 32, 4);
		healthBar = screen.cut("/art/effects/bar_green.png", 32, 4);
		healthBar_Outline = screen.cut("/art/effects/bar_outline.png", 32, 4);
		healthBar_Underlay = screen.cut("/art/effects/bar_green_underlay.png", 32, 4);
		sprintBar = screen.cut("/art/effects/sprint_bar.png", 32, 4);
		dish = screen.cut("/art/mob/dish001.png", 6, 12);
		// different backgrounds
		int w, h;
		w = CharacterButton.BUTTON_WIDTH;
		h = CharacterButton.BUTTON_HEIGHT;
		backCharacterButton = new AbstractBitmap[3];
		backCharacterButton[0] = screen.createBitmap(w, h);
		backCharacterButton[0].fill(0, 0, w, h, 0xff522d16);
		backCharacterButton[0].fill(1, 1, w - 2, h - 2, 0);
		backCharacterButton[1] = screen.createBitmap(w, h);
		backCharacterButton[1].fill(0, 0, w, h, 0xff26150a);
		backCharacterButton[1].fill(1, 1, w - 2, h - 2, 0);
		backCharacterButton[2] = screen.createBitmap(w, h);
		backCharacterButton[2].fill(0, 0, w, h, 0xff26150a);
		backCharacterButton[2].fill(1, 1, w - 2, h - 2, 0xff3a210f);
		w = LevelEditorButton.WIDTH;
		h = LevelEditorButton.HEIGHT;
		backLevelEditorButton = new AbstractBitmap[3];
		backLevelEditorButton[0] = screen.createBitmap(w, h);
		backLevelEditorButton[0].fill(0, 0, w, h, 0xff522d16);
		backLevelEditorButton[0].fill(1, 1, w - 2, h - 2, 0);
		backLevelEditorButton[1] = screen.createBitmap(w, h);
		backLevelEditorButton[1].fill(0, 0, w, h, 0xff26150a);
		backLevelEditorButton[1].fill(1, 1, w - 2, h - 2, 0);
		backLevelEditorButton[2] = screen.createBitmap(w, h);
		backLevelEditorButton[2].fill(0, 0, w, h, 0xff26150a);
		backLevelEditorButton[2].fill(1, 1, w - 2, h - 2, 0xff3a210f);
		w = LevelButton.WIDTH;
		h = LevelButton.HEIGHT;
		backLevelButton = new AbstractBitmap[3];
		backLevelButton[0] = screen.createBitmap(w, h);
		backLevelButton[0].fill(0, 0, w, h, 0xff522d16);
		backLevelButton[0].fill(1, 1, w - 2, h - 2, 0);
		backLevelButton[1] = screen.createBitmap(w, h);
		backLevelButton[1].fill(0, 0, w, h, 0xff26150a);
		backLevelButton[1].fill(1, 1, w - 2, h - 2, 0);
		backLevelButton[2] = screen.createBitmap(w, h);
		backLevelButton[2].fill(0, 0, w, h, 0xff26150a);
		backLevelButton[2].fill(1, 1, w - 2, h - 2, 0xff3a210f);
	}

	/**
	 * Load a bitmap resource by name
	 * 
	 * @param string Resource name
	 * @return BufferedImage on success, null on error
	 */
	private static BufferedImage loadBufferedImage(String string) {
		try {
			BufferedImage bi = ImageIO.read(MojamComponent.class.getResource(string));
			return bi;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
