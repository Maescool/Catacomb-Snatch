package com.mojang.mojam.tiled;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

//import org.newdawn.slick.SlickException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
	 * A group of objects on the map (objects layer)
	 *
	 * @author liamzebedee
	 */
public class ObjectGroup {
	  /** The index of this group */
	  public int index;
	  /** The name of this group - read from the XML */
	  public String name;
	  /** The Objects of this group*/
	  public ArrayList<GroupObject> objects;
	  /** The width of this layer */
	  public int width;
	  /** The height of this layer */
	  public int height;
	  /** The mapping between object names and offsets */
	  private HashMap<String,Integer> nameToObjectMap = new HashMap<String,Integer>();
	  /** the properties of this group */
	  public Properties props;
	  /** The TiledMap of which this ObjectGroup belongs to */
	  TiledMap map;
	  
	  /**
	   * Create a new group based on the XML definition
	   * @author kulpae || liamzebedee
	   * @param element The XML element describing the layer
	   * @param map The map to which the ObjectGroup belongs
	   * @throws SlickException Indicates a failure to parse the XML group
	   */
	  
	  public ObjectGroup(Element element, TiledMap map) throws Exception {
		  	this.map = map;
		  	
			name = element.getAttribute("name");
			width = Integer.parseInt(element.getAttribute("width"));
			height = Integer.parseInt(element.getAttribute("height"));
			objects = new ArrayList<GroupObject>();

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
			
			NodeList objectNodes = element.getElementsByTagName("object");
			for (int i = 0; i < objectNodes.getLength(); i++) {
				Element objElement = (Element) objectNodes.item(i);
				GroupObject object = null;
				if(map != null){
					object = new GroupObject(objElement, map);
				}
				else{
					object = new GroupObject(objElement);
				}
				object.index = i;
				objects.add(object);
			}
	}
	  
	/**
	 * Gets an object by its name
	 * @author liamzebedee
	 * @param objectName The name of the object
	*/
	public GroupObject getObject(String objectName){
		GroupObject g = (GroupObject) this.objects.get(
				this.nameToObjectMap.get(objectName));
		return g;
	}
	  
	/**
	 * Gets all objects of a specific type on a layer
	 * 
	 * @author liamzebedee
	 * @param type The name of the type
	*/
	public ArrayList<GroupObject> getObjectsOfType(String type){
		ArrayList<GroupObject> foundObjects = new ArrayList<GroupObject>();
		for(GroupObject object : this.objects){
			if(object.type.equals(type)){
				foundObjects.add(object);
			}
		}
		return foundObjects;
	}
	
	/**
	* Removes an object
	* 
	* @author liamzebedee
	* @param objectName The name of the object
	*/
	public void removeObject(String objectName){
		int objectOffset = this.nameToObjectMap.get(objectName);
		this.objects.remove(objectOffset);
	}
	  
	/**
	 * Sets the mapping from object names to their offsets
	 * 
	 * @author liamzebedee
	 * @param map The name of the map
	*/
	public void setObjectNameMapping(HashMap<String,Integer> map){
	  this.nameToObjectMap = map;
	}
	
	/**
	 * Adds an object to the object group
	 * 
	 * @author liamzebedee
	 * @param object The object to be added
	 */
	public void addObject(GroupObject object){
		this.objects.add(object);
		this.nameToObjectMap.put(object.name, this.objects.size()); 
	}
	
	/**
	 * Gets all the objects from this group
	 * 
	 * @author liamzebedee
	 * @return 
	 */
	public ArrayList<GroupObject> getObjects(){
		return this.objects;
	}
}
