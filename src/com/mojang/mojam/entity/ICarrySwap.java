package com.mojang.mojam.entity;

import com.mojang.mojam.entity.building.Building;

/**
 * Interface defines if
 * 1. it can carry a Building
 * 2. if it can, can it swap with another entity
 * 
 * @author Morgan
 *
 */
public interface ICarrySwap {

	public boolean isCarrying();
	
	public boolean canCarry(Building b);
	
	public boolean canPickup(Building b);
	
	public Building getCarrying();
		
	public void pickup(Building b);
	
	public Building tryToSwap(Building b);	
	
}
