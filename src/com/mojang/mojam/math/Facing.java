package com.mojang.mojam.math;

public class Facing {
	public static final int NORTH = 0;
	public static final int SOUTH = 1;
	public static final int WEST = 2;
	public static final int EAST = 3;

	public static Vec2 getVector(int dir) {
		return getVector(dir, 1.0);
	}

	public static Vec2 getVector(int dir, double length) {
		switch(dir) {
		case NORTH: return new Vec2(0, -length);
		case SOUTH: return new Vec2(0, +length);
		case WEST : return new Vec2(-length, 0);
		case EAST : return new Vec2(+length, 0);
		}
		return null;
	}
}
