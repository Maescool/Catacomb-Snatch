package com.mojang.mojam.tiled;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

//import org.newdawn.slick.Image;
//import org.newdawn.slick.util.ResourceLoader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * This class is intended to parse TilED maps. TilED is a generic tool for tile map editing and can
 * be found at:
 * 
 * http://mapeditor.org/
 * 
 * @author kevin
 */
public class TiledMap {
	/** Indicates if we're running on a headless system */
	private static boolean headless;
	
	/**
	 * Indicate if we're running on a headless system where we'd just like to load
	 * the data model.
	 * 
	 * @param h True if we're running on a headless system
	 */
	private static void setHeadless(boolean h) {
		headless = h;
	}
	
	/** The width of the map */
	protected int width;
	/** The height of the map */
	protected int height;
	/** The width of the tiles used on the map */
	protected int tileWidth;
	/** The height of the tiles used on the map */
	protected int tileHeight;
	
	/** The location prefix where we can find tileset images */
	protected String tilesLocation;
	
	/** the properties of the map */
	protected Properties props;
	
	/** The list of tilesets defined in the map */
	protected ArrayList<TileSet> tileSets = new ArrayList<TileSet>();
	/** The list of layers defined in the map */
	protected ArrayList<Layer> layers = new ArrayList<Layer>();
	/** The list of object-groups defined in the map */
    protected ArrayList<ObjectGroup> objectGroups = new ArrayList<ObjectGroup>();
	
	/**
	 * Load a tile map from an arbitary input stream
	 * 
	 * @param in The input stream to load from
	 * @throws Exception Indicates a failure to load the tilemap
	 */
	public TiledMap(InputStream in) throws Exception {
		load(in, "");
	}

	/**
	 * Load a tile map from an arbitary input stream
	 * 
	 * @param in The input stream to load from
	 * @param tileSetsLocation The location at which we can find tileset images
	 * @throws Exception Indicates a failure to load the tilemap
	 */
	public TiledMap(InputStream in, String tileSetsLocation) throws Exception {
		load(in, tileSetsLocation);
	}
	
	/**
	 * Get the location of the tile images specified
	 * 
	 * @return The location of the tile images specified as a resource reference prefix
	 */
	public String getTilesLocation() {
		return tilesLocation;
	}
	
	/**
     * Get the index of the layer with given name
     * 
     * @param name The name of the tile to search for
     * @return The index of the layer or -1 if there is no layer with given name
     */
   public int getLayerIndex(String name) {
      //int idx = 0;
      
      for (int i=0;i<layers.size();i++) {
         Layer layer = (Layer) layers.get(i);
         
         if (layer.name.equals(name)) {
            return i;
         }
      }
      
      return -1;
   }
   
	/**
	 * Get the width of the map
	 * 
	 * @return The width of the map (in tiles)
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Get the height of the map
	 * 
	 * @return The height of the map (in tiles)
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Get the height of a single tile
	 * 
	 * @return The height of a single tile (in pixels)
	 */
	public int getTileHeight() {
		return tileHeight;
	}

	/**
	 * Get the width of a single tile
	 * 
	 * @return The height of a single tile (in pixels)
	 */
	public int getTileWidth() {
		return tileWidth;
	}
	   
	/**
	 * Get the global ID of a tile at specified location in the map
	 * 
	 * @param x
	 *            The x location of the tile
	 * @param y
	 *            The y location of the tile
	 * @param layerIndex
	 *            The index of the layer to retireve the tile from
	 * @return The global ID of the tile
	 */
	public int getTileId(int x,int y,int layerIndex) {
		Layer layer = (Layer) layers.get(layerIndex);
		return layer.getTileID(x,y);
	}
	
	/**
	 * Set the global ID of a tile at specified location in the map
	 * @param x
	 *            The x location of the tile
	 * @param y
	 *            The y location of the tile
	 * @param layerIndex
	 *            The index of the layer to set the new tileid
	 * @param tileid
	 *            The tileid to be set
	 */
	public void setTileId(int x, int y, int layerIndex, int tileid) {
		Layer layer = (Layer) layers.get(layerIndex);
		layer.setTileID(x, y, tileid);
	}
	
	/**
	 * Get a property given to the map. Note that this method will
	 * not perform well and should not be used as part of the default code
	 * path in the game loop.
	 * 
	 * @param propertyName The name of the property of the map to retrieve
	 * @param def The default value to return
	 * @return The value assigned to the property on the map (or the default value if none is supplied)
	 */
	public String getMapProperty(String propertyName, String def) {
		if (props == null)
			return def;
		return props.getProperty(propertyName, def);
	}
	
	/**
	 * Get a property given to a particular layer. Note that this method will
	 * not perform well and should not be used as part of the default code
	 * path in the game loop.
	 * 
	 * @param layerIndex The index of the layer to retrieve
	 * @param propertyName The name of the property of this layer to retrieve
	 * @param def The default value to return
	 * @return The value assigned to the property on the layer (or the default value if none is supplied)
	 */
	public String getLayerProperty(int layerIndex, String propertyName, String def) {
		Layer layer = (Layer) layers.get(layerIndex);
		if (layer == null || layer.props == null)
			return def;
		return layer.props.getProperty(propertyName, def);
	}
	
	
	/**
	 * Get a propety given to a particular tile. Note that this method will
	 * not perform well and should not be used as part of the default code
	 * path in the game loop.
	 * 
	 * @param tileID The global ID of the tile to retrieve
	 * @param propertyName The name of the property to retireve
	 * @param def The default value to return
	 * @return The value assigned to the property on the tile (or the default value if none is supplied)
	 */
	public String getTileProperty(int tileID, String propertyName, String def) {
		if (tileID == 0) {
			return def;
		}
		
		TileSet set = findTileSet(tileID);
		
		Properties props = set.getProperties(tileID);
		if (props == null) {
			return def;
		}
		return props.getProperty(propertyName, def);
	}
	
	/**
	 * Retrieve a count of the number of layers available
	 * 
	 * @return The number of layers available in this map
	 */
	public int getLayerCount() {
		return layers.size();
	}
	
	/**
	 * Load a TilED map
	 * 
	 * @param in The input stream from which to load the map
	 * @param tileSetsLocation The location from which we can retrieve tileset images
	 * @throws Exception Indicates a failure to parse the map or find a tileset
	 */
	private void load(InputStream in, String tileSetsLocation) throws Exception {
		tilesLocation = tileSetsLocation;
		
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);
			DocumentBuilder builder = factory.newDocumentBuilder();
			builder.setEntityResolver(new EntityResolver() {
				public InputSource resolveEntity(String publicId,
						String systemId) throws SAXException, IOException {					
					return new InputSource(new ByteArrayInputStream(new byte[0]));
				}
			});
			
			Document doc = builder.parse(in);
			Element docElement = doc.getDocumentElement();
			
			String orient = docElement.getAttribute("orientation");
			if (!orient.equals("orthogonal")) {
				throw new Exception("Only orthogonal maps supported, found: "+orient);
			}
			
			width = Integer.parseInt(docElement.getAttribute("width"));
			height = Integer.parseInt(docElement.getAttribute("height"));
			tileWidth = Integer.parseInt(docElement.getAttribute("tilewidth"));
			tileHeight = Integer.parseInt(docElement.getAttribute("tileheight"));
			
			// now read the map properties
			Element propsElement = (Element) docElement.getElementsByTagName("properties").item(0);
			if (propsElement != null) {
				NodeList properties = propsElement.getElementsByTagName("property");
				if (properties != null) {
					props = new Properties();
					for (int p = 0; p < properties.getLength();p++) {
						Element propElement = (Element) properties.item(p);
						
						String name = propElement.getAttribute("name");
						String value = propElement.getAttribute("value");		
						props.setProperty(name, value);
					}
				}
			}
			
			NodeList layerNodes = docElement.getElementsByTagName("layer");
			for (int i=0;i<layerNodes.getLength();i++) {
				Element current = (Element) layerNodes.item(i);
				Layer layer = new Layer(this, current);
				layer.index = i;
				layers.add(layer);
			}
			
			// acquire object-groups
			NodeList objectGroupNodes = docElement.getElementsByTagName("objectgroup");
			     
			for (int i=0;i<objectGroupNodes.getLength();i++) {
				Element current = (Element) objectGroupNodes.item(i);
				ObjectGroup objectGroup = new ObjectGroup(current,this);
				objectGroup.index = i;
			        
				objectGroups.add(objectGroup);
			}
		} catch (Exception e) {
			throw new Exception("Failed to parse tilemap - be sure it was saved with gzip compression", e);
		}
	}
	
	/**
	 * Retrieve the number of tilesets available in this map
	 * 
	 * @return The number of tilesets available in this map
	 */
	public int getTileSetCount() {
		return tileSets.size();
	}
	
	/**
	 * Get a tileset at a particular index in the list of sets for this map
	 * 
	 * @param index The index of the tileset. 
	 * @return The TileSet requested
	 */
	public TileSet getTileSet(int index) {
		return (TileSet) tileSets.get(index);
	}
	
	/**
	 * Get a tileset by a given global ID
	 * 
	 * @param gid The global ID of the tileset to retrieve
	 * @return The tileset requested or null if no tileset matches
	 */
	public TileSet getTileSetByGID(int gid) {
		for (int i=0;i<tileSets.size();i++) {
			TileSet set = (TileSet) tileSets.get(i);
			
			if (set.contains(gid)) {
				return set;
			}
		}
		
		return null;
	}
	
	/**
	 * Find a tile for a given global tile id
	 * 
	 * @param gid The global tile id we're looking for
	 * @return The tileset in which that tile lives or null if the gid is not defined
	 */
	public TileSet findTileSet(int gid) {
		for (int i=0;i<tileSets.size();i++) {
			TileSet set = (TileSet) tileSets.get(i);
			
			if (set.contains(gid)) {
				return set;
			}
		}
		
		return null;
	}
	
	/**
	 * Overrideable to allow other sprites to be rendered between lines of the
	 * map
	 * 
	 * @param visualY The visual Y coordinate, i.e. 0->height
	 * @param mapY The map Y coordinate, i.e. y->y+height
	 * @param layer The layer being rendered
	 */
	protected void renderedLine(int visualY, int mapY,int layer) {
	}
	
	/**
	 * Returns the number of object-groups defined in the map.
	 * @return Number of object-groups on the map
	 */
	public int getObjectGroupCount() {
		return objectGroups.size();
	}
	
	/**
	 * Returns the number of objects of a specific object-group.
	 * @param groupID The index of this object-group
	 * @return Number of the objects in the object-group or -1, when error occurred.
	 */
	public int getObjectCount(int groupID) {
		if (groupID >= 0 && groupID < objectGroups.size()) {
			ObjectGroup grp = (ObjectGroup) objectGroups.get(groupID);
			return grp.objects.size();
		}
		return -1;
	}
	
	/**
	 * Return the name of a specific object from a specific group.
	 * @param groupID Index of a group
	 * @param objectID Index of an object
	 * @return The name of an object or null, when error occurred
	 */
	public String getObjectName(int groupID, int objectID) {
		if (groupID >= 0 && groupID < objectGroups.size()) {
			ObjectGroup grp = (ObjectGroup) objectGroups.get(groupID);
			if (objectID >= 0 && objectID < grp.objects.size()) {
				GroupObject object = (GroupObject) grp.objects.get(objectID);
				return object.name;
			}
		}
		return null;
	}
	
	/**
	 * Return the type of an specific object from a specific group.
	 * @param groupID Index of a group
	 * @param objectID Index of an object
	 * @return The type of an object or null, when error occurred
	 */
	public String getObjectType(int groupID, int objectID) {
		if (groupID >= 0 && groupID < objectGroups.size()) {
			ObjectGroup grp = (ObjectGroup) objectGroups.get(groupID);
			if (objectID >= 0 && objectID < grp.objects.size()) {
				GroupObject object = (GroupObject) grp.objects.get(objectID);
				return object.type;
			}
		}
		return null;
	}
	
	/**
	 * Returns the x-coordinate of a specific object from a specific group.
	 * @param groupID Index of a group
	 * @param objectID Index of an object
	 * @return The x-coordinate of an object, or -1, when error occurred
	 */
	public int getObjectX(int groupID, int objectID) {
		if (groupID >= 0 && groupID < objectGroups.size()) {
			ObjectGroup grp = (ObjectGroup) objectGroups.get(groupID);
			if (objectID >= 0 && objectID < grp.objects.size()) {
				GroupObject object = (GroupObject) grp.objects.get(objectID);
				return object.x;
			}
		}
		return -1;
	}
	
	/**
	 * Returns the y-coordinate of a specific object from a specific group.
	 * @param groupID Index of a group
	 * @param objectID Index of an object
	 * @return The y-coordinate of an object, or -1, when error occurred
	 */
	public int getObjectY(int groupID, int objectID) {
		if (groupID >= 0 && groupID < objectGroups.size()) {
			ObjectGroup grp = (ObjectGroup) objectGroups.get(groupID);
			if (objectID >= 0 && objectID < grp.objects.size()) {
				GroupObject object = (GroupObject) grp.objects.get(objectID);
				return object.y;
			}
		}
		return -1;
	}
	
	/**
	 * Returns the width of a specific object from a specific group.
	 * @param groupID Index of a group
	 * @param objectID Index of an object
	 * @return The width of an object, or -1, when error occurred
	 */
	public int getObjectWidth(int groupID, int objectID) {
		if (groupID >= 0 && groupID < objectGroups.size()) {
			ObjectGroup grp = (ObjectGroup) objectGroups.get(groupID);
			if (objectID >= 0 && objectID < grp.objects.size()) {
				GroupObject object = (GroupObject) grp.objects.get(objectID);
				return object.width;
			}
		}
		return -1;
	}
	
	/**
	 * Returns the height of a specific object from a specific group.
	 * @param groupID Index of a group
	 * @param objectID Index of an object
	 * @return The height of an object, or -1, when error occurred
	 */
	public int getObjectHeight(int groupID, int objectID) {
		if (groupID >= 0 && groupID < objectGroups.size()) {
			ObjectGroup grp = (ObjectGroup) objectGroups.get(groupID);
			if (objectID >= 0 && objectID < grp.objects.size()) {
				GroupObject object = (GroupObject) grp.objects.get(objectID);
				return object.height;
			}
		}
		return -1;
	}
	
	/**
	 * Retrieve the image source property for a given object
	 * 
	 * @param groupID Index of a group
	 * @param objectID Index of an object
	 * @return The image source reference or null if one isn't defined
	 */
	public String getObjectImage(int groupID, int objectID) {
		if (groupID >= 0 && groupID < objectGroups.size()) {
			ObjectGroup grp = (ObjectGroup) objectGroups.get(groupID);
			if (objectID >= 0 && objectID < grp.objects.size()) {
				GroupObject object = (GroupObject) grp.objects.get(objectID);
				
				if (object == null) {
					return null;
				}
				
				return object.image;
			}
		}
		
		return null;
	}
	
	/**
	 * Looks for a property with the given name and returns it's value. If no property is found,
	 * def is returned.
	 * @param groupID Index of a group
	 * @param objectID Index of an object
	 * @param propertyName Name of a property
	 * @param def default value to return, if no property is found
	 * @return The value of the property with the given name or def, if there is no property with that name.
	 */
	public String getObjectProperty(int groupID, int objectID,
			String propertyName, String def) {
		if (groupID >= 0 && groupID < objectGroups.size()) {
			ObjectGroup grp = (ObjectGroup) objectGroups.get(groupID);
			if (objectID >= 0 && objectID < grp.objects.size()) {
				GroupObject object = (GroupObject) grp.objects.get(objectID);
				
				if (object == null) {
					return def;
				}
				if (object.props == null) {
					return def;
				}
				
				return object.props.getProperty(propertyName, def);
			}
		}
		return def;
	}
	
		
	
}
