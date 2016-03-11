package com.mojang.mojam.tiled;

import java.util.Properties;

//import org.newdawn.slick.Image;
//import org.newdawn.slick.SlickException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * An object from a object-group on the map
 * 
 * @author kulpae || liamzebedee
 */
public class GroupObject {
  /** The index of this object */
  public int index;
  /** The name of this object - read from the XML */
  public String name = "";
  /** The type of this object - read from the XML */
  public String type = "";
  /** The x-coordinate of this object */
  public int x;
  /** The y-coordinate of this object */
  public int y;
  /** The width of this object */
  public int width = 0;
  /** The height of this object */
  public int height = 0;
  /** The image source */
  String image;
  /** the properties of this group */
  public Properties props;
  /** The gid reference to the image */
  public int gid = -1;
  /** Indicates if this is an image object */
  public boolean isImageObject = false;
  /** The map this object belongs to */
  TiledMap map;

 /**
 * Create a new group based on the XML definition
 * @author kulpae
 * @param element The XML element describing the layer
 * @throws SlickException Indicates a failure to parse the XML group
 */
 public GroupObject(Element element) throws Exception {
		if(element.getAttribute("gid") != ""){
			gid = Integer.parseInt(element.getAttribute("gid"));
			isImageObject = true;
		}
		if(isImageObject){
			if(element.getAttribute("width") != ""){
				width = Integer.parseInt(element.getAttribute("width"));
			}
			if(element.getAttribute("height") != ""){
				height = Integer.parseInt(element.getAttribute("height"));
			}
			if(element.getAttribute("name") != ""){
				name = element.getAttribute("name");
			}
			if(element.getAttribute("type") != ""){
				type = element.getAttribute("type");
			}
		}
		else{
			width = Integer.parseInt(element.getAttribute("width"));
			height = Integer.parseInt(element.getAttribute("height"));
			name = element.getAttribute("name");
			type = element.getAttribute("type");
		}
		x = Integer.parseInt(element.getAttribute("x"));
		y = Integer.parseInt(element.getAttribute("y"));
		
		
		
		// now read the layer properties
		Element propsElement = (Element) element.getElementsByTagName(
				"properties").item(0);
		if (propsElement != null) {
			NodeList properties = propsElement
					.getElementsByTagName("property");
			if (properties != null) {
				props = new Properties();
				for (int p = 0; p < properties.getLength(); p++) {
					Element propElement = (Element) properties.item(p);

					String name = propElement.getAttribute("name");
					String value = propElement.getAttribute("value");
					props.setProperty(name, value);
				}
			}
		}
	}
 
 /**
  * Create a new group based on the XML definition
  * @author kulpae || liamzebedee
  * @param element The XML element describing the layer
  * @param map The map this object belongs to
  * @throws SlickException Indicates a failure to parse the XML group
  */
  public GroupObject(Element element, TiledMap map) throws Exception {
	  this.map = map;
	  if(element.getAttribute("gid") != ""){
			gid = Integer.parseInt(element.getAttribute("gid"));
			isImageObject = true;
		}
		if(isImageObject){
			if(element.getAttribute("width") != ""){
				width = Integer.parseInt(element.getAttribute("width"));
			}
			if(element.getAttribute("height") != ""){
				height = Integer.parseInt(element.getAttribute("height"));
			}
			if(element.getAttribute("name") != ""){
				name = element.getAttribute("name");
			}
			if(element.getAttribute("type") != ""){
				type = element.getAttribute("type");
			}
		}
		else{
			width = Integer.parseInt(element.getAttribute("width"));
			height = Integer.parseInt(element.getAttribute("height"));
			name = element.getAttribute("name");
			type = element.getAttribute("type");
		}
		x = Integer.parseInt(element.getAttribute("x"));
		y = Integer.parseInt(element.getAttribute("y"));
		
		
 		
 		// now read the layer properties
 		Element propsElement = (Element) element.getElementsByTagName(
 				"properties").item(0);
 		if (propsElement != null) {
 			NodeList properties = propsElement
 					.getElementsByTagName("property");
 			if (properties != null) {
 				props = new Properties();
 				for (int p = 0; p < properties.getLength(); p++) {
 					Element propElement = (Element) properties.item(p);

 					String name = propElement.getAttribute("name");
 					String value = propElement.getAttribute("value");
 					props.setProperty(name, value);
 				}
 			}
 		}
 	}
 
 	/**
 	 * Creates a new GroupObject, using pre existing variables instead of XML elements
 	 * If inPixels is true, then all the integer values are treated in pixels. If false, then it treats it as Tiled map co-ordinates
 	 * 
 	 * @author liamzebedee
	 * @param name The name of the object
	 * @param x The x co-ordinate of the object
	 * @param y The y co-ordinate of the object
	 * @param type The type of this object
	 * @param width The width of this object
	 * @param height The height of this object
	 * @param props The properties of this object
	 * @param inPixels If the integer values specified are in pixels or not
 	 */
 	public GroupObject(int x,int y,String type,int width,int height,Properties props,String name,boolean inPixels){
 		if(inPixels){
     		this.x = x;
     		this.y = y;
        	this.width = width;
     		this.height = height;
        }
        else{
        	this.x = x*32;
     		this.y = y*32;
        	this.width = width*32;
     		this.height = height*32;
        }
 		this.type = type;
 		this.props = props;
 		this.name = name;
 	}
 	
 	/**
 	 * Creates a new GroupObject, using pre existing variables instead of XML elements
 	 * If inPixels is true, then all the integer values are treated in pixels. If false, then it treats it as Tiled map co-ordinates
 	 * 
 	 * @author liamzebedee
	 * @param name The name of the object
	 * @param x The x co-ordinate of the object
	 * @param y The y co-ordinate of the object
	 * @param type The type of this object
	 * @param width The width of this object
	 * @param height The height of this object
	 * @param props The properties of this object
	 * @param inPixels If the integer values specified are in pixels or not
	 * @param gid If this is an image object, the gid is set. If this isn't an image object, the gid is -1
	 * @param map The map this object belongs to
 	 */
 	public GroupObject(int x,int y,String type,int width,int height,Properties props,String name,boolean inPixels, int gid, TiledMap map){
 		this.map = map;
 		if(gid != -1){
 			this.gid = gid;
 			this.isImageObject = true;
 		}
 		if(inPixels){
     		this.x = x;
     		this.y = y;
        	this.width = width;
     		this.height = height;
        }
        else{
        	this.x = x*32;
     		this.y = y*32;
        	this.width = width*32;
     		this.height = height*32;
        }
 		this.type = type;
 		this.props = props;
 		this.name = name;
 	}
 	
	/**
	 * Puts a property to an object
	 * @author liamzebedee
	 * @param propertyKey The key of the property to be put to the object
	 * @param propertyValue The value mappped to the key of the property to be put to the object
	 */
 	public void putProperty(String propertyKey,String propertyValue){
 		this.props.put(propertyKey, propertyValue);
 	}
 	
	/**
	 * Puts a property to an object
	 * @author liamzebedee
	 * @param propertyKey The key of the property to be put to the object
	 * @param propertyValue The value mappped to the key of the property to be put to the object
	 */
 	public void removeProperty(String propertyKey){
 		this.props.remove(propertyKey);
 	}
}
   