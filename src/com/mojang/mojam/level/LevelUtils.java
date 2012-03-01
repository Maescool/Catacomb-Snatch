package com.mojang.mojam.level;

import com.mojang.mojam.entity.Entity;
import com.mojang.mojam.entity.building.SpawnerForBat;
import com.mojang.mojam.entity.building.SpawnerForMummy;
import com.mojang.mojam.entity.building.SpawnerForScarab;
import com.mojang.mojam.entity.building.SpawnerForSnake;
import com.mojang.mojam.entity.building.TreasurePile;
import com.mojang.mojam.entity.building.Turret;
import com.mojang.mojam.entity.building.TurretTeamOne;
import com.mojang.mojam.entity.building.TurretTeamTwo;
import com.mojang.mojam.entity.loot.Loot;
import com.mojang.mojam.entity.mob.SpikeTrap;
import com.mojang.mojam.entity.mob.Team;
import com.mojang.mojam.level.tile.DestroyableWallTile;
import com.mojang.mojam.level.tile.FloorTile;
import com.mojang.mojam.level.tile.HoleTile;
import com.mojang.mojam.level.tile.SandTile;
import com.mojang.mojam.level.tile.Tile;
import com.mojang.mojam.level.tile.UnbreakableRailTile;
import com.mojang.mojam.level.tile.UnpassableSandTile;
import com.mojang.mojam.level.tile.WallTile;

public class LevelUtils {

	public static Tile getNewTileFromColor(int color) {
		
		Tile tile = null;
		
		switch (color) {
		case SandTile.COLOR:
			tile = new SandTile();
			break;			
		case UnbreakableRailTile.COLOR:
			tile = new UnbreakableRailTile(new FloorTile());
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
		default:
			tile = new FloorTile();
			break;	
		}
		
		return tile;
	}
	
	
	public static Entity getNewEntityFromColor(int color, int x, int y) {
		
		Entity entity = null;
		
		switch (color) {			
		case SpikeTrap.COLOR:
			entity = new SpikeTrap(x * Tile.WIDTH,y * Tile.HEIGHT);
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
		default:
			entity = null;
			break;
		}
		
		return entity;
	}
	
}
