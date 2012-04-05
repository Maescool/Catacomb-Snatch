package com.mojang.mojam.level;

import com.mojang.mojam.entity.Entity;
import com.mojang.mojam.entity.building.ShopItemBomb;
import com.mojang.mojam.entity.building.ShopItemHarvester;
import com.mojang.mojam.entity.building.ShopItemRaygun;
import com.mojang.mojam.entity.building.ShopItemRifle;
import com.mojang.mojam.entity.building.ShopItemShotgun;
import com.mojang.mojam.entity.building.ShopItemSoldier;
import com.mojang.mojam.entity.building.ShopItemTurret;
import com.mojang.mojam.entity.building.SpawnerForBat;
import com.mojang.mojam.entity.building.SpawnerForMummy;
import com.mojang.mojam.entity.building.SpawnerForScarab;
import com.mojang.mojam.entity.building.SpawnerForSnake;
import com.mojang.mojam.entity.building.TreasureChest;
import com.mojang.mojam.entity.building.TreasurePile;
import com.mojang.mojam.entity.building.Turret;
import com.mojang.mojam.entity.building.TurretTeamOne;
import com.mojang.mojam.entity.building.TurretTeamTwo;
import com.mojang.mojam.entity.loot.Loot;
import com.mojang.mojam.entity.mob.Bat;
import com.mojang.mojam.entity.mob.Mummy;
import com.mojang.mojam.entity.mob.Pharao;
import com.mojang.mojam.entity.mob.Scarab;
import com.mojang.mojam.entity.mob.Snake;
import com.mojang.mojam.entity.mob.SpikeTrap;
import com.mojang.mojam.entity.mob.Team;
import com.mojang.mojam.level.tile.DestroyableWallTile;
import com.mojang.mojam.level.tile.DropTrap;
import com.mojang.mojam.level.tile.FloorTile;
import com.mojang.mojam.level.tile.HoleTile;
import com.mojang.mojam.level.tile.PlayerBaseLeft;
import com.mojang.mojam.level.tile.PlayerBaseRight;
import com.mojang.mojam.level.tile.PlayerRailTile;
import com.mojang.mojam.level.tile.PlayerSpawn;
import com.mojang.mojam.level.tile.RailTile;
import com.mojang.mojam.level.tile.SandTile;
import com.mojang.mojam.level.tile.Tile;
import com.mojang.mojam.level.tile.UnbreakableRailTile;
import com.mojang.mojam.level.tile.UnpassableSandTile;
import com.mojang.mojam.level.tile.WallTile;

public class LevelUtils {
	
	private static final int TILESET_FLOOR_BASE_ID = 1;
	private static final int TILESET_OVERLAY_BASE_ID = 65;
	private static final int TILESET_WALL_BASE_ID = 129;
	private static final int TILESET_PLAYER1_BASE_ID = 193;
	private static final int TILESET_PLAYER2_BASE_ID = 257;
	

	public static Tile getNewTileFromColor(int color) {
		
		Tile tile = null;
		
		switch (color) {
		case SandTile.COLOR:
			tile = new SandTile();
			break;			
		case UnbreakableRailTile.COLOR:
			tile = new UnbreakableRailTile();
			break;			
		case UnpassableSandTile.COLOR:
			tile = new UnpassableSandTile();
			break;
		case DestroyableWallTile.COLOR:
			tile = new DestroyableWallTile();
			break;
		case HoleTile.COLOR:
			tile = new HoleTile();
			break;
		case WallTile.COLOR:
			tile =  new WallTile();
			break;
		case DropTrap.COLOR:
			tile =  new DropTrap();
			break;
		default:
			tile = new FloorTile();
			break;	
		}
		
		return tile;
	}
	
	public static Object getNewObjectFromId(int x, int y, int id) {
		
		Object obj = null;
		
		switch (id) {
		
		//Floor IDs
		case TILESET_FLOOR_BASE_ID:
			obj = new FloorTile();
			break;		
		case TILESET_FLOOR_BASE_ID + 1:
			obj = new SandTile();
			break;
		case TILESET_FLOOR_BASE_ID + 2:
			obj = new UnpassableSandTile();
			break;
		case TILESET_FLOOR_BASE_ID + 3:
			obj = new HoleTile();
			break;		
		case TILESET_FLOOR_BASE_ID + 8:
			obj = new DropTrap();
			break;
			
		//Overlay IDs
		case TILESET_OVERLAY_BASE_ID:
			obj = new TreasurePile(x * Tile.WIDTH+Tile.WIDTH/2,y * Tile.HEIGHT + 12);
			break;
		case TILESET_OVERLAY_BASE_ID + 1:
			obj = new RailTile();
			break;
		case TILESET_OVERLAY_BASE_ID + 2:
			obj = new UnbreakableRailTile();
			break;
		case TILESET_OVERLAY_BASE_ID + 3:
			obj = new SpikeTrap(x * Tile.WIDTH,y * Tile.HEIGHT);
			break;
		case TILESET_OVERLAY_BASE_ID + 4:
			obj = new TreasureChest(x * Tile.WIDTH+Tile.WIDTH/2,y * Tile.HEIGHT+Tile.HEIGHT/2 -5, Team.Neutral, 800);
			break;
		case TILESET_OVERLAY_BASE_ID + 8:
			obj = new Bat(x * Tile.WIDTH+Tile.WIDTH/2,y * Tile.HEIGHT+Tile.HEIGHT/2);
			break;
		case TILESET_OVERLAY_BASE_ID + 9:
			obj = new Snake(x * Tile.WIDTH+Tile.WIDTH/2,y * Tile.HEIGHT+Tile.HEIGHT/2);
			break;
		case TILESET_OVERLAY_BASE_ID + 10:
			obj = new Scarab(x * Tile.WIDTH+Tile.WIDTH/2,y * Tile.HEIGHT+Tile.HEIGHT/2);
			break;
		case TILESET_OVERLAY_BASE_ID + 11:
			obj = new Mummy(x * Tile.WIDTH+Tile.WIDTH/2,y * Tile.HEIGHT+Tile.HEIGHT/2);
			break;
		case TILESET_OVERLAY_BASE_ID + 12:
			obj = new Pharao(x * Tile.WIDTH+Tile.WIDTH/2,y * Tile.HEIGHT+Tile.HEIGHT/2);
			break;
		case TILESET_OVERLAY_BASE_ID + 16:
			obj = new SpawnerForBat(x * Tile.WIDTH+Tile.WIDTH/2,y * Tile.HEIGHT+Tile.HEIGHT/2);
			break;
		case TILESET_OVERLAY_BASE_ID + 17:
			obj = new SpawnerForSnake(x * Tile.WIDTH+Tile.WIDTH/2,y * Tile.HEIGHT+Tile.HEIGHT/2);
			break;
		case TILESET_OVERLAY_BASE_ID + 18:
			obj = new SpawnerForScarab(x * Tile.WIDTH+Tile.WIDTH/2,y * Tile.HEIGHT+Tile.HEIGHT/2);
			break;
		case TILESET_OVERLAY_BASE_ID + 19:
			obj = new SpawnerForMummy(x * Tile.WIDTH+Tile.WIDTH/2,y * Tile.HEIGHT+Tile.HEIGHT/2);
			break;
		case TILESET_OVERLAY_BASE_ID + 24:
			//Do not darken this tile
			obj = new Integer(32);
			break;	

		//Wall IDs
		case TILESET_WALL_BASE_ID:
			obj =  new WallTile();
			break;
		case TILESET_WALL_BASE_ID + 1:
			obj = new DestroyableWallTile();
			break;

		//Player 1 IDs
			
		//Player 1 Spawn
		case TILESET_PLAYER1_BASE_ID:
			obj =  new PlayerSpawn(0, Team.Team1);
			break;
		case TILESET_PLAYER1_BASE_ID + 1:
			obj =  new PlayerSpawn(1, Team.Team1);
			break;
		case TILESET_PLAYER1_BASE_ID + 2:
			obj =  new PlayerSpawn(2, Team.Team1);
			break;
		case TILESET_PLAYER1_BASE_ID + 3:
			obj =  new PlayerSpawn(3, Team.Team1);
			break;
		case TILESET_PLAYER1_BASE_ID + 8:
			obj =  new PlayerSpawn(4, Team.Team1);
			break;
		case TILESET_PLAYER1_BASE_ID + 9:
			obj =  new PlayerSpawn(5, Team.Team1);
			break;
		case TILESET_PLAYER1_BASE_ID + 10:
			obj =  new PlayerSpawn(6, Team.Team1);
			break;
		case TILESET_PLAYER1_BASE_ID + 11:
			obj =  new PlayerSpawn(7, Team.Team1);
			break;
		case TILESET_PLAYER1_BASE_ID + 16:
			obj =  new PlayerSpawn(8, Team.Team1);
			break;
		case TILESET_PLAYER1_BASE_ID + 17:
			obj =  new PlayerSpawn(9, Team.Team1);
			break;
		case TILESET_PLAYER1_BASE_ID + 18:
			obj =  new PlayerSpawn(10, Team.Team1);
			break;
		case TILESET_PLAYER1_BASE_ID + 19:
			obj =  new PlayerSpawn(11, Team.Team1);
			break;
		
		//Player 1 Base Left
		case TILESET_PLAYER1_BASE_ID + 4:
			obj =  new PlayerBaseLeft(0, Team.Team1);
			break;
		case TILESET_PLAYER1_BASE_ID + 5:
			obj =  new PlayerBaseLeft(1, Team.Team1);
			break;
		case TILESET_PLAYER1_BASE_ID + 12:
			obj =  new PlayerBaseLeft(2, Team.Team1);
			break;
		case TILESET_PLAYER1_BASE_ID + 13:
			obj =  new PlayerBaseLeft(3, Team.Team1);
			break;
		case TILESET_PLAYER1_BASE_ID + 20:
			obj =  new PlayerBaseLeft(4, Team.Team1);
			break;
		case TILESET_PLAYER1_BASE_ID + 21:
			obj =  new PlayerBaseLeft(5, Team.Team1);
			break;
		
		//Player 1 Base Right
		case TILESET_PLAYER1_BASE_ID + 6:
			obj =  new PlayerBaseRight(0, Team.Team1);
			break;
		case TILESET_PLAYER1_BASE_ID + 7:
			obj =  new PlayerBaseRight(1, Team.Team1);
			break;
		case TILESET_PLAYER1_BASE_ID + 14:
			obj =  new PlayerBaseRight(2, Team.Team1);
			break;
		case TILESET_PLAYER1_BASE_ID + 15:
			obj =  new PlayerBaseRight(3, Team.Team1);
			break;
		case TILESET_PLAYER1_BASE_ID + 22:
			obj =  new PlayerBaseRight(4, Team.Team1);
			break;
		case TILESET_PLAYER1_BASE_ID + 23:
			obj =  new PlayerBaseRight(5, Team.Team1);
			break;
			

		case TILESET_PLAYER1_BASE_ID + 24:
			obj =  new ShopItemTurret(x * Tile.WIDTH+Tile.WIDTH/2, y * Tile.HEIGHT+Tile.HEIGHT/2, Team.Team1);
			break;
		case TILESET_PLAYER1_BASE_ID + 25:
			obj =  new ShopItemHarvester(x * Tile.WIDTH+Tile.WIDTH/2, y * Tile.HEIGHT+Tile.HEIGHT/2, Team.Team1);
			break;
		case TILESET_PLAYER1_BASE_ID + 26:
			obj =  new ShopItemBomb(x * Tile.WIDTH+Tile.WIDTH/2, y * Tile.HEIGHT+Tile.HEIGHT/2, Team.Team1);
			break;
		case TILESET_PLAYER1_BASE_ID + 27:
			obj =  new PlayerRailTile(Team.Team1);
			break;
		case TILESET_PLAYER1_BASE_ID + 32:
			obj =  new ShopItemRifle(x * Tile.WIDTH+Tile.WIDTH/2, y * Tile.HEIGHT+Tile.HEIGHT/2, Team.Team1);
			break;
		case TILESET_PLAYER1_BASE_ID + 33:
			obj =  new ShopItemShotgun(x * Tile.WIDTH+Tile.WIDTH/2, y * Tile.HEIGHT+Tile.HEIGHT/2, Team.Team1);
			break;
		case TILESET_PLAYER1_BASE_ID + 34:
			obj =  new ShopItemRaygun(x * Tile.WIDTH+Tile.WIDTH/2, y * Tile.HEIGHT+Tile.HEIGHT/2, Team.Team1);
			break;
		case TILESET_PLAYER1_BASE_ID + 35:
			obj =  new ShopItemSoldier(x * Tile.WIDTH+Tile.WIDTH/2, y * Tile.HEIGHT+Tile.HEIGHT/2, Team.Team1);
			break;
		//Player 2 IDs
		
		//Player 2 Spawn
		case TILESET_PLAYER2_BASE_ID:
			obj =  new PlayerSpawn(0, Team.Team2);
			break;
		case TILESET_PLAYER2_BASE_ID + 1:
			obj =  new PlayerSpawn(1, Team.Team2);
			break;
		case TILESET_PLAYER2_BASE_ID + 2:
			obj =  new PlayerSpawn(2, Team.Team2);
			break;
		case TILESET_PLAYER2_BASE_ID + 3:
			obj =  new PlayerSpawn(3, Team.Team2);
			break;
		case TILESET_PLAYER2_BASE_ID + 8:
			obj =  new PlayerSpawn(4, Team.Team2);
			break;
		case TILESET_PLAYER2_BASE_ID + 9:
			obj =  new PlayerSpawn(5, Team.Team2);
			break;
		case TILESET_PLAYER2_BASE_ID + 10:
			obj =  new PlayerSpawn(6, Team.Team2);
			break;
		case TILESET_PLAYER2_BASE_ID + 11:
			obj =  new PlayerSpawn(7, Team.Team2);
			break;
		case TILESET_PLAYER2_BASE_ID + 16:
			obj =  new PlayerSpawn(8, Team.Team2);
			break;
		case TILESET_PLAYER2_BASE_ID + 17:
			obj =  new PlayerSpawn(9, Team.Team2);
			break;
		case TILESET_PLAYER2_BASE_ID + 18:
			obj =  new PlayerSpawn(10, Team.Team2);
			break;
		case TILESET_PLAYER2_BASE_ID + 19:
			obj =  new PlayerSpawn(11, Team.Team2);
			break;
		
		//Player 2 Base Left
		case TILESET_PLAYER2_BASE_ID + 4:
			obj =  new PlayerBaseLeft(0, Team.Team2);
			break;
		case TILESET_PLAYER2_BASE_ID + 5:
			obj =  new PlayerBaseLeft(1, Team.Team2);
			break;
		case TILESET_PLAYER2_BASE_ID + 12:
			obj =  new PlayerBaseLeft(2, Team.Team2);
			break;
		case TILESET_PLAYER2_BASE_ID + 13:
			obj =  new PlayerBaseLeft(3, Team.Team2);
			break;
		case TILESET_PLAYER2_BASE_ID + 20:
			obj =  new PlayerBaseLeft(4, Team.Team2);
			break;
		case TILESET_PLAYER2_BASE_ID + 21:
			obj =  new PlayerBaseLeft(5, Team.Team2);
			break;
		
		//Player 2 Base Right
		case TILESET_PLAYER2_BASE_ID + 6:
			obj =  new PlayerBaseRight(0, Team.Team2);
			break;
		case TILESET_PLAYER2_BASE_ID + 7:
			obj =  new PlayerBaseRight(1, Team.Team2);
			break;
		case TILESET_PLAYER2_BASE_ID + 14:
			obj =  new PlayerBaseRight(2, Team.Team2);
			break;
		case TILESET_PLAYER2_BASE_ID + 15:
			obj =  new PlayerBaseRight(3, Team.Team2);
			break;
		case TILESET_PLAYER2_BASE_ID + 22:
			obj =  new PlayerBaseRight(4, Team.Team2);
			break;
		case TILESET_PLAYER2_BASE_ID + 23:
			obj =  new PlayerBaseRight(5, Team.Team2);
			break;
			
			
		case TILESET_PLAYER2_BASE_ID + 24:
			obj =  new ShopItemTurret(x * Tile.WIDTH+Tile.WIDTH/2, y * Tile.HEIGHT+Tile.HEIGHT/2, Team.Team2);
			break;
		case TILESET_PLAYER2_BASE_ID + 25:
			obj =  new ShopItemHarvester(x * Tile.WIDTH+Tile.WIDTH/2, y * Tile.HEIGHT+Tile.HEIGHT/2, Team.Team2);
			break;
		case TILESET_PLAYER2_BASE_ID + 26:
			obj =  new ShopItemBomb(x * Tile.WIDTH+Tile.WIDTH/2, y * Tile.HEIGHT+Tile.HEIGHT/2, Team.Team2);
			break;
		case TILESET_PLAYER2_BASE_ID + 27:
			obj =  new PlayerRailTile(Team.Team2);
			break;
		case TILESET_PLAYER2_BASE_ID + 32:
			obj =  new ShopItemRifle(x * Tile.WIDTH+Tile.WIDTH/2, y * Tile.HEIGHT+Tile.HEIGHT/2, Team.Team2);
			break;
		case TILESET_PLAYER2_BASE_ID + 33:
			obj =  new ShopItemShotgun(x * Tile.WIDTH+Tile.WIDTH/2, y * Tile.HEIGHT+Tile.HEIGHT/2, Team.Team2);
			break;
		case TILESET_PLAYER2_BASE_ID + 34:
			obj =  new ShopItemRaygun(x * Tile.WIDTH+Tile.WIDTH/2, y * Tile.HEIGHT+Tile.HEIGHT/2, Team.Team2);
			break;			
		case TILESET_PLAYER2_BASE_ID + 35:
			obj =  new ShopItemSoldier(x * Tile.WIDTH+Tile.WIDTH/2, y * Tile.HEIGHT+Tile.HEIGHT/2, Team.Team1);
			break;		

		default:
			break;	
		}
		
		return obj;
	}
	
	
	public static Entity getNewEntityFromColor(int color, int x, int y) {
		
		Entity entity = null;
		
		switch (color) {
		case SpikeTrap.COLOR:
			entity = new SpikeTrap(x * Tile.WIDTH,y * Tile.HEIGHT);
			break;
		case SpikeTrap.COLOR1:
			entity = new SpikeTrap(x * Tile.WIDTH,y * Tile.HEIGHT, 30);
			break;
		case SpikeTrap.COLOR2:
			entity = new SpikeTrap(x * Tile.WIDTH,y * Tile.HEIGHT, 60);
			break;
		case SpawnerForBat.COLOR:
			entity = new SpawnerForBat(x * Tile.WIDTH+Tile.WIDTH/2,y * Tile.HEIGHT+Tile.HEIGHT/2);
			break;
		case SpawnerForSnake.COLOR:
			entity = new SpawnerForSnake(x * Tile.WIDTH+Tile.WIDTH/2,y * Tile.HEIGHT+Tile.HEIGHT/2);
			break;			
		case SpawnerForMummy.COLOR:
			entity = new SpawnerForMummy(x * Tile.WIDTH+Tile.WIDTH/2,y * Tile.HEIGHT+Tile.HEIGHT/2);
			break;
		case SpawnerForScarab.COLOR:
			entity = new SpawnerForScarab(x * Tile.WIDTH+Tile.WIDTH/2,y * Tile.HEIGHT+Tile.HEIGHT/2);
			break;
		case Bat.COLOR:
			entity = new Bat(x * Tile.WIDTH+Tile.WIDTH/2,y * Tile.HEIGHT+Tile.HEIGHT/2);
			break;
		case Snake.COLOR:
			entity = new Snake(x * Tile.WIDTH+Tile.WIDTH/2,y * Tile.HEIGHT+Tile.HEIGHT/2);
			break;			
		case Mummy.COLOR:
			entity = new Mummy(x * Tile.WIDTH+Tile.WIDTH/2,y * Tile.HEIGHT+Tile.HEIGHT/2);
			break;
		case Pharao.COLOR:
            entity = new Pharao(x * Tile.WIDTH+Tile.WIDTH/2,y * Tile.HEIGHT+Tile.HEIGHT/2);
            break;
        case Scarab.COLOR:
			entity = new Scarab(x * Tile.WIDTH+Tile.WIDTH/2,y * Tile.HEIGHT+Tile.HEIGHT/2);
			break;
		case Turret.COLOR:
			entity = new Turret(x * Tile.WIDTH+Tile.WIDTH/2,y * Tile.HEIGHT+Tile.HEIGHT/2,Team.Neutral);
			break;
		case TurretTeamOne.COLOR:
			entity = new Turret(x * Tile.WIDTH+Tile.WIDTH/2,y * Tile.HEIGHT+Tile.HEIGHT/2,Team.Team1);
			break;
		case TurretTeamTwo.COLOR:
			entity = new Turret(x * Tile.WIDTH+Tile.WIDTH/2,y * Tile.HEIGHT+Tile.HEIGHT/2,Team.Team2);
			break;
		case TreasurePile.COLOR:
			entity = new TreasurePile(x * Tile.WIDTH+Tile.WIDTH/2,y * Tile.HEIGHT + 12);
			break;
		case 0xff100700: //TODO do this the same way as above
			for (int i = 0; i < 4; i++) {
				double dir = i * Math.PI * 2 / 8;
				entity = new Loot(x * Tile.WIDTH+Tile.WIDTH/2,y * Tile.HEIGHT+Tile.HEIGHT/2, Math.cos(dir), Math.sin(dir), 200, false);
			}
			break;
		case TreasureChest.COLOR:
			entity = new TreasureChest(x * Tile.WIDTH+Tile.WIDTH/2,y * Tile.HEIGHT+Tile.HEIGHT/2 -5, Team.Neutral, 800);
			break;	
		default:
			entity = null;
			break;
		}
		
		return entity;
	}
	
}
