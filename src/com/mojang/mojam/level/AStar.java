package com.mojang.mojam.level;

import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Random;

import com.mojang.mojam.entity.mob.Mob;
import com.mojang.mojam.level.tile.Tile;
import com.mojang.mojam.math.Vec2;
import com.mojang.mojam.network.TurnSynchronizer;

public class AStar {
	Level level;
	Mob mob;

	HashMap<String, Node> nodes = new HashMap<String, Node>();

	private static final Vec2[] dirs = { new Vec2(-1, 0), new Vec2(1, 0),
			new Vec2(0, 1), new Vec2(0, -1) };

	public AStar(Level level, Mob mob) {
		this.level = level;
		this.mob = mob;
	}

	private boolean canWalk(Vec2 gridPos) {
		Tile tile = level.getTile((int) gridPos.x, (int) gridPos.y);
		if (tile == null)
			return false;
		return tile.canPass(mob);
	}

	private void addNeighbors(Node n) {
		Vec2 p = n.pos;
		for (Vec2 d : dirs)
			if (canWalk(p.add(d)))
				n.addNeighbor(getNode(p.add(d)));
	}

	private Node getNode(Vec2 p) {
		String hash = Node.getHash(p);
		Node n = nodes.get(hash);
		return n == null ? createNode(p) : n;
	}

	private Node createNode(Vec2 p) {
		Node n = new Node(p);
		nodes.put(Node.getHash(p), n);
		return n;
	}

	private Path _reconstructPath(Node goalNode) {
		Path path = new Path(true);
		Node node = goalNode;
		while (node != null) {
			path.addNodeFront(node);
			node = node.__parent;
		}
		return path;
	}

	public Path getPath(Vec2 gridStart, Vec2 gridGoal) {
		return getPathMods(gridStart, gridGoal, 0, 0);
	}

	public Path getPathMods(Vec2 gridStart, Vec2 gridGoal,
			double avoidWallsModifier, double randomDistanceModifier) {
	
		double distanceMod = 1;
		Random random = TurnSynchronizer.synchedRandom;
	
		nodes.clear();
	
		if (!canWalk(gridStart))
			return new Path(false);
	
		Node start = createNode(gridStart);
		Node goal = createNode(gridGoal);
		if (start.pos.equals(goal.pos))
			return new Path(true);
	
		PriorityQueue<Node> queue = new PriorityQueue<Node>();
		queue.add(start);
	
		Path bestPath = null;
		while (queue.size() != 0) {
	
			Node current = queue.poll();
			if (current.__visited)
				continue;
	
			if (current == goal) {
				bestPath = _reconstructPath(goal);
				break;
			}
	
			addNeighbors(current);
			current.__visited = true;
	
			distanceMod = 1;
	
			distanceMod *= avoidWallsModifier
					* (5 - current.getNeighbors().size());
	
			for (Node neighbor : current.getNeighbors()) {
				if (neighbor.__visited)
					continue;
				if (!canWalk(neighbor.pos)) {
					continue;
				}
	
				double distance = current.__pathDistance
						+ current.pos.dist(neighbor.pos);
	
				if (randomDistanceModifier > 0) {
					distanceMod += (random.nextDouble() - 0.5)
							* randomDistanceModifier;
				}
	
				distance = distance * distanceMod;
	
				if (neighbor.__parent != null
						&& distance >= neighbor.__pathDistance)
					continue;
	
				neighbor.__pathDistance = distance;
				neighbor.__heuristicDistance = neighbor.pos.dist(goal.pos)
						+ distance;
				if (neighbor.__parent == null) {
					neighbor.__priority = neighbor.__heuristicDistance;
					queue.add(neighbor);
				} else
					neighbor.__priority = neighbor.__heuristicDistance;
	
				neighbor.__parent = current;
			}
		}
	
		return bestPath == null ? new Path(false) : bestPath;
	}
}
