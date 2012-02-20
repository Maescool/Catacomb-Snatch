package com.mojang.mojam.level;

import java.util.Comparator;

import com.mojang.mojam.entity.Entity;

public class EntityComparator implements Comparator<Entity> {
	public int compare(Entity e0, Entity e1) {
		if (e0.pos.y < e1.pos.y) return -1;
		if (e0.pos.y > e1.pos.y) return +1;
		if (e0.pos.x < e1.pos.x) return -1;
		if (e0.pos.x > e1.pos.x) return +1;
		return 0;
	}

}
