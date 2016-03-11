package com.mojang.mojam.tiled;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.zip.GZIPInputStream;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A layer of tiles on the map
 *
 * @author kevin || liamzebedee
 */
public class Layer {
	/** The code used to decode Base64 encoding */
	private static byte[] baseCodes = new byte[256];

	/**
	 * Static initialiser for the codes created against Base64
	 */
	static {
		for (int i = 0; i < 256; i++)
			baseCodes[i] = -1;
		for (int i = 'A'; i <= 'Z'; i++)
			baseCodes[i] = (byte) (i - 'A');
		for (int i = 'a'; i <= 'z'; i++)
			baseCodes[i] = (byte) (26 + i - 'a');
		for (int i = '0'; i <= '9'; i++)
			baseCodes[i] = (byte) (52 + i - '0');
		baseCodes['+'] = 62;
		baseCodes['/'] = 63;
	}
	
	/** The map this layer belongs to */
	private final TiledMap map;
	/** The index of this layer */
	public int index;
	/** The name of this layer - read from the XML */
	public String name;
	/** The tile data representing this data, index 0 = tileset, index 1 = tile id */
	public int[][][] data;
	/** The width of this layer */
	public int width;
	/** The height of this layer */
	public int height;
	
	/** the properties of this layer */
	public Properties props;
	
	/** The TiledMapPlus of this layer */
	private TiledMap tmap;
	
	/**
	 * Create a new layer based on the XML definition
	 * 
	 * @param element The XML element describing the layer
	 * @param map The map this layer is part of
	 * @throws Exception Indicates a failure to parse the XML layer
	 */
	public Layer(TiledMap map, Element element) throws Exception {
		this.map = map;
		name = element.getAttribute("name");
		width = Integer.parseInt(element.getAttribute("width"));
		height = Integer.parseInt(element.getAttribute("height"));
		data = new int[width][height][3];

		// now read the layer properties
		Element propsElement = (Element) element.getElementsByTagName("properties").item(0);
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

		Element dataNode = (Element) element.getElementsByTagName("data").item(0);
		String encoding = dataNode.getAttribute("encoding");
		String compression = dataNode.getAttribute("compression");
		
		if (encoding.equals("base64") && compression.equals("gzip")) {
			try {
                Node cdata = dataNode.getFirstChild();
                char[] enc = cdata.getNodeValue().trim().toCharArray();
                byte[] dec = decodeBase64(enc);
                GZIPInputStream is = new GZIPInputStream(new ByteArrayInputStream(dec));
                
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int tileId = 0;
                        tileId |= is.read();
                        tileId |= is.read() <<  8;
                        tileId |= is.read() << 16;
                        tileId |= is.read() << 24;

                        if (tileId == 0) {
	                        data[x][y][0] = -1;
	                        data[x][y][1] = 0;
	                        data[x][y][2] = 0;
                        } else {
	                        TileSet set = map.findTileSet(tileId);

	                        if (set != null) {
		                        data[x][y][0] = set.index;
		                        data[x][y][1] = tileId - set.firstGID;
	                        }
	                        data[x][y][2] = tileId;
                        }
                    }
                }
			} catch (IOException e) {
				throw new Exception("Unable to decode base 64 block");
			}
		} else {
			throw new Exception("Unsupport tiled map type: "+encoding+","+compression+" (only gzip base64 supported)");
		}
	}
	
	/**
	 * Get the gloal ID of the tile at the specified location in
	 * this layer
	 * 
	 * @param x The x coorindate of the tile
	 * @param y The y coorindate of the tile
	 * @return The global ID of the tile
	 */
	public int getTileID(int x, int y) {
		return data[x][y][2];
	}
	
	/**
	 * Set the global tile ID at a specified location
	 * 
	 * @param x The x location to set
	 * @param y The y location to set
	 * @param tile The tile value to set
	 */
	public void setTileID(int x, int y, int tile) {
        if (tile == 0) {
            data[x][y][0] = -1;
            data[x][y][1] = 0;
            data[x][y][2] = 0;
        } else {
            TileSet set = map.findTileSet(tile);
            
            data[x][y][0] = set.index; //tileSetIndex
            data[x][y][1] = tile - set.firstGID; //localID
            data[x][y][2] = tile; //globalID
        }
	}
	
	/**
	 * Decode a Base64 string as encoded by TilED
	 * 
	 * @param data The string of character to decode
	 * @return The byte array represented by character encoding
	 */
    private byte[] decodeBase64(char[] data) {
		int temp = data.length;
		for (int ix = 0; ix < data.length; ix++) {
			if ((data[ix] > 255) || baseCodes[data[ix]] < 0) {
				--temp; 
			}
		}

		int len = (temp / 4) * 3;
		if ((temp % 4) == 3)
			len += 2;
		if ((temp % 4) == 2)
			len += 1;

		byte[] out = new byte[len];

		int shift = 0;
		int accum = 0;
		int index = 0;

		for (int ix = 0; ix < data.length; ix++) {
			int value = (data[ix] > 255) ? -1 : baseCodes[data[ix]];

			if (value >= 0) {
				accum <<= 6;
				shift += 6;
				accum |= value;
				if (shift >= 8) {
					shift -= 8;
					out[index++] = (byte) ((accum >> shift) & 0xff);
				}
			}
		}

		if (index != out.length) {
			throw new RuntimeException(
					"Data length appears to be wrong (wrote " + index
							+ " should be " + out.length + ")");
		}

		return out;
	}
    
    /**
     * Gets all Tiles from this layer, formatted into Tile objects
	 * Can only be used if the layer was loaded using TiledMapPlus
     * 
     * @author liamzebedee
	 * @throws Exception 
     */
    public ArrayList<Tile> getTiles() throws Exception{ 
		if(tmap == null){
			throw new Exception("This method can only be used with Layers loaded using TiledMapPlus");
		}
    	ArrayList<Tile> tiles = new ArrayList<Tile>();
    	for (int x = 0; x < this.width; x++) {
			for (int y = 0; y < this.height; y++) {
				String tilesetName = tmap.tileSets.get(this.data[x][y][0]).name;
				Tile t = new Tile(x, y, this.name, y, tilesetName);
				tiles.add(t);
			}
		}
    	return tiles;
    }
	
	/**
	 * Removes a tile
	 * 
	 * @author liamzebedee
	 * @param x Tile X
	 * @param y Tile Y
	*/
	public void removeTile(int x, int y){
		this.data[x][y][0] = -1;
	}    
    
}