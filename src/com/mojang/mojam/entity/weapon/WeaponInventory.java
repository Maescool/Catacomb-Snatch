package com.mojang.mojam.entity.weapon;

import java.util.ArrayList;
import java.util.List;

public class WeaponInventory {

	private List<IWeapon> weaponList;
	/**
	 * A list of the players current weapon(s). Can contain only one instance of each weapon
	 */
	
	public WeaponInventory() {
		weaponList = new ArrayList<IWeapon>();
	}
	
	/**
	 * Appends the element to the inventory only if it contains no duplicates.
	 * @param weapon
	 * @return true if weapon was successfully added, false otherwise.
	 */
	public boolean add(IWeapon weapon) {
		if(weaponList.contains(weapon)) return false;
		weaponList.add(weapon);
		return true;
	}
	
	public boolean isEmpty() {
		if(weaponList.isEmpty())
			return true;
		return false;
	}

	public int size() {
		return weaponList.size();
	}

	/**
	 * Returns null if the slot does not exist
	 * @param i
	 * @return
	 */
	public IWeapon get(int i) {
		if(weaponList.size() > i) return weaponList.get(i);
		return null;
	}

	public void clearAll() {
		weaponList.clear();
	}
	
}