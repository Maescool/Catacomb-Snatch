package com.mojang.mojam.math;

public class Mth {
	static public int clamp(int value, int low, int high) {
		if (value < low)
			return low;
		return value > high ? high : value;
	}
}
