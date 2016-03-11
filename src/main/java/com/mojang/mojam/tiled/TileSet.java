package com.mojang.mojam.tiled;

import java.util.HashMap;
import java.util.Properties;


import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * A holder for tileset information
 *
 * @author kevin
 */
public class TileSet {
	/** The map this tileset was loaded as part of */
	@SuppressWarnings("unused")
	private final TiledMap map;
	/** The index of the tile set */
	public int index;
	/** The name of the tile set */
	public String name;
	/** The first global tile id in the set */
	public int firstGID;
	/** The local global tile id in the set */
	public int lastGID = Integer.MAX_VALUE;
	/** The width of the tiles */
	public int tileWidth;
	/** The height of the tiles */
	public int tileHeight;
	/** The image containing the tiles */
	//public SpriteSheet tiles;
	
	/** The number of tiles across the sprite sheet */
	public int tilesAcross;
	/** The number of tiles down the sprite sheet */
	public int tilesDown;
	
	/** The properties for each tile */
	private HashMap<Integer, Properties> props = new HashMap<Integer, Properties>();
	/** The padding of the tiles */
	public int tileSpacing = 0;
	/** The margin of the tileset */
	public int tileMargin = 0;
	/** The image for this tileset */
	public String imageref;
	
	
	/**
	 * Create a tile set based on an XML definition
	 * 
	 * @param element The XML describing the tileset
	 * @param map The map this tileset was loaded from (gives context to paths)
	 * @param loadImage True if the images should be loaded, false if we're running somewhere images can't be loaded
	 * @throws Exception Indicates a failure to parse the tileset
	 */
	
	public TileSet(TiledMap map, Element element, boolean loadImage) throws Exception {
		this.map = map;
		firstGID = Integer.parseInt(element.getAttribute("firstgid"));
		//String source = element.getAttribute("source");

		name = element.getAttribute("name");
        String tileWidthString = element.getAttribute("tilewidth");
        String tileHeightString = element.getAttribute("tileheight");
        if(tileWidthString.length() == 0 || tileHeightString.length() == 0) {
            throw new Exception("TiledMap requires that the map be created with tilesets that use a " +
                    "single image.  Check the WiKi for more complete information.");
        }
		tileWidth = Integer.parseInt(tileWidthString);
		tileHeight = Integer.parseInt(tileHeightString);
		
		String sv = element.getAttribute("spacing");
		if ((sv != null) && (!sv.equals(""))) {
			tileSpacing = Integer.parseInt(sv);
        }
         
        String mv = element.getAttribute("margin");
		if ((mv != null) && (!mv.equals(""))) {
			tileMargin = Integer.parseInt(mv);
		}
          
		//NodeList list = element.getElementsByTagName("image");
		//Element imageNode = (Element) list.item(0);
		//String ref = imageNode.getAttribute("source");
		
		NodeList pElements = element.getElementsByTagName("tile");
		for (int i=0;i<pElements.getLength();i++) {
			Element tileElement = (Element) pElements.item(i);
			
			int id = Integer.parseInt(tileElement.getAttribute("id"));
			id += firstGID;
			Properties tileProps = new Properties();
			
			Element propsElement = (Element) tileElement.getElementsByTagName("properties").item(0);
			NodeList properties = propsElement.getElementsByTagName("property");
			for (int p=0;p<properties.getLength();p++) {
				Element propElement = (Element) properties.item(p);
				
				String name = propElement.getAttribute("name");
				String value = propElement.getAttribute("value");
				
				tileProps.setProperty(name, value);
			}
			
			props.put(new Integer(id), tileProps);
		}
	}
	
	
	/**
	 * Get the width of each tile in this set
	 * 
	 * @return The width of each tile in this set
	 */
	public int getTileWidth() {
		return tileWidth;
	}

	/**
	 * Get the height of each tile in this set
	 * 
	 * @return The height of each tile in this set
	 */
	public int getTileHeight() {
		return tileHeight;
	}
	
	/**
	 * Get the spacing between tiles in this set
	 * 
	 * @return The spacing between tiles in this set 
	 */
	public int getTileSpacing() {
		return tileSpacing;
	}

	/**
	 * Get the margin around tiles in this set
	 * 
	 * @return The maring around tiles in this set
	 */
	public int getTileMargin() {
		return tileMargin;
	}
	
	/**
	 * Set the image to use for this sprite sheet image to use for this tileset
	 * 
	 * @param image The image to use for this tileset
	 */
	/*
	public void setTileSetImage(Image image) {
		
		tiles = new SpriteSheet(image, tileWidth, tileHeight, tileSpacing, tileMargin); 
		tilesAcross = tiles.getHorizontalCount();
		tilesDown = tiles.getVerticalCount();

		if (tilesAcross <= 0) {
			tilesAcross = 1;
		}
		if (tilesDown <= 0) {
			tilesDown = 1;
		}

		lastGID = (tilesAcross * tilesDown) + firstGID - 1;
	}
	*/
	
	/**
	 * Get the properties for a specific tile in this tileset
	 * 
	 * @param globalID The global ID of the tile whose properties should be retrieved
	 * @return The properties for the specified tile, or null if no properties are defined
	 */
	public Properties getProperties(int globalID) {
		return (Properties) props.get(new Integer(globalID));
	}
	
	/**
	 * Get the x position of a tile on this sheet
	 * 
     * @param id The tileset specific ID (i.e. not the global one)
	 * @return The index of the tile on the x-axis
	 */
	public int getTileX(int id) {
		return id % tilesAcross;
	}

	/**
	 * Get the y position of a tile on this sheet
	 * 
     * @param id The tileset specific ID (i.e. not the global one)
	 * @return The index of the tile on the y-axis
	 */
	public int getTileY(int id) {	
		return id / tilesAcross;
	}

	/**
	 * Set the limit of the tiles in this set
	 * 
	 * @param limit The limit of the tiles in this set
	 */
	public void setLimit(int limit) {
		lastGID = limit;
	}
	
	/**
	 * Check if this tileset contains a particular tile
	 * 
	 * @param gid The global id to seach for
	 * @return True if the ID is contained in this tileset
	 */
	public boolean contains(int gid) {
		return (gid >= firstGID) && (gid <= lastGID);
	}
}