package com.mojang.mojam.entity;

/**
 * Simple interface used as a cut down Observer type call back function
 * so when an entity dies it can notify something else.
 * 
 * currently use in Soldier and ShopItemSoldier
 * 
 * @see Soldier
 * @see ShopItemSoldier
 * @author Morgan
 */
public interface IRemoveEntityNotify {

	public void removeEntityNotice(Entity e);

}
