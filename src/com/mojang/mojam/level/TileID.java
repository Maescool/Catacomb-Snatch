package com.mojang.mojam.level;

import java.util.HashMap;

import com.mojang.mojam.entity.building.TreasurePile;
import com.mojang.mojam.entity.mob.Team;
import com.mojang.mojam.level.tile.*;

public class TileID {
	
	private static HashMap<Short, Class<? extends Tile>> shortToTileMap = new HashMap<Short, Class<? extends Tile>>();
	private static HashMap<Class<? extends Tile>, Short> tileToShortMap = new HashMap<Class<? extends Tile>, Short>();
	private static HashMap<Integer, Class<? extends Tile>> colorToTileMap = new HashMap<Integer, Class<? extends Tile>>();
	private static HashMap<Class<? extends Tile>, Integer> tileToColorMap = new HashMap<Class<? extends Tile>, Integer>();
	
	static {
		registerTile((short) 0, FloorTile.class, 0xffffff);
		registerTile((short) 1, HoleTile.class, 0x000000);
		registerTile((short) 2, RailTile.class, 0x767676);
		registerTile((short) 3, SandTile.class, 0xA8A800);
		registerTile((short) 4, UnbreakableRailTile.class, 0x969696);
		registerTile((short) 5, UnpassableSandTile.class, 0x888800);
		registerTile((short) 6, WallTile.class, 0xff0000);
	}
	
	/**
	 * This must be called once so that tiles can be sent via- multiplayer.
	 * They will need a constructor with no arguments
	 */
	public static void registerTile(short id, Class<? extends Tile> tileclass, int color){
		shortToTileMap.put(id, tileclass);
		tileToShortMap.put(tileclass, id);
		colorToTileMap.put(color, tileclass);
		tileToColorMap.put(tileclass, color);
	}
	
	public static short tileToShort(Tile tile){
		if(!tileToShortMap.containsKey(tile.getClass())) return 0;
		return tileToShortMap.get(tile.getClass());
	}
	
	public static Tile shortToTile(short i, Level l, int x, int y){
		return classToTile((Class<? extends Tile>)shortToTileMap.get(i));
	}
	
	public static Tile classToTile(Class<? extends Tile> tileclass){
		Tile tile = new FloorTile();
		try
        {
            if(tileclass == UnbreakableRailTile.class){
            	tile = (Tile)tileclass.getConstructor(new Class[] {
            			Tile.class }).newInstance(new Object[] {
                        		 new FloorTile() });
            }
            else if (tileclass != null)
            {
                tile = (Tile)tileclass.getConstructor().newInstance();
            }
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }
        return tile;
	}
	
	public static Tile colorToTile(int col){
		Tile tile = new FloorTile();
		if (col == 0xffff00) {
			tile = null;
		} else {
			tile = classToTile(colorToTileMap.get(col));
		}
		return tile;
	}
	
	public static int tileToColor(Tile tile){
		return tileToColorMap.get(tile.getClass());
	}	
}
