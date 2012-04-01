package com.mojang.mojam.level;

import java.util.ArrayList;
import java.util.List;

import com.mojang.mojam.math.Vec2;

public class Node implements Comparable<Node> {
	public Vec2 pos;
	public List<Node> neighbors = new ArrayList<Node>();

	public Node(Vec2 pos) {
		this.pos = pos.clone().floor();
	}

	public void addNeighbor(Node n) {
		neighbors.add(n);
	}

	public List<Node> getNeighbors() {
		return neighbors;
	}

	public int compare(Node n1, Node n2) {
		if (n1.__priority == n2.__priority)
			return 0;
		return n1.__priority < n2.__priority ? -1 : 1;
	}

	public int compareTo(Node o) {
		return compare(this, o);
	}

	public static String getHash(Vec2 pos) {
		return (int) pos.x + "_" + (int) pos.y;
	}

	public boolean __visited = false;
	public double __pathDistance = 0;
	public double __heuristicDistance = Double.MAX_VALUE;
	public Node __parent = null;
	public double __priority = 0;

}
