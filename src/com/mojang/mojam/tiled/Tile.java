package com.mojang.mojam.tiled;

public class Tile{
    /**
	 * A class for holding information about a particular tile on a particular layer
	 * @author liamzebedee
	 */
	/** The x co-ordinate of the tile */
	public int x;
	
	/** The y co-ordinate of the tile */
	public int y;
	
	/** The layer name on which this tile is on */
	public String layerName;
	
	/** The Global ID of the Tile */
	public int gid;
	
	/** The name of the tile's tileset */
	public String tilesetName;
	
	/**
	 * Constructor for a Tile
	 * 
	 * @author liamzebedee
	 * @param x The x co-ordinate of the tile
	 * @param y The y co-ordinate of the tile
	 * @param layerName The layer name on which this tile is on
	 * @param id The Global ID of the Tile
	 * @param tileset The name of the Tile's tileset
	 */
	public Tile(int x,int y,String layerName,int gid,String tileset){
		this.x = x;
		this.y = y;
		this.layerName = layerName;
		this.gid = gid;
		this.tilesetName = tileset;
	}
	
}