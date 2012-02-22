package com.mojang.mojam.level;

import java.util.HashMap;

import com.mojang.mojam.level.tile.*;

public class TileID {
	
	private static HashMap<Short, Class> shortToTileMap = new HashMap<Short, Class>();
	private static HashMap<Class, Short> tileToShortMap = new HashMap<Class, Short>();
	
	static {
		registerTile((short) 0, FloorTile.class);
		registerTile((short) 1, HoleTile.class);
		registerTile((short) 2, RailTile.class);
		registerTile((short) 3, SandTile.class);
		registerTile((short) 4, UnbreakableRailTile.class);
		registerTile((short) 5, UnpassableSandTile.class);
		registerTile((short) 6, WallTile.class);
	}
	
	/**
	 * This must be called once so that tiles can be sent via- multiplayer.
	 * They will need a constructor with no arguments
	 */
	public static void registerTile(short id, Class tileclass){
		shortToTileMap.put(id, tileclass);
		tileToShortMap.put(tileclass, id);
	}
	
	public static short tileToShort(Tile tile){
		if(!tileToShortMap.containsKey(tile.getClass())) return 0;
		return tileToShortMap.get(tile.getClass());
	}
	
	public static Tile shortToTile(short i, Level l, int x, int y){
		Tile tile = new FloorTile();
		try
        {
            Class class1 = (Class)shortToTileMap.get(i);
            if(class1 == UnbreakableRailTile.class){
            	tile = (Tile)class1.getConstructor(new Class[] {
            			Tile.class }).newInstance(new Object[] {
                        		 new FloorTile() });
            }
            else if (class1 != null)
            {
                tile = (Tile)class1.getConstructor().newInstance();
            }
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }
        return tile;
	}
}
