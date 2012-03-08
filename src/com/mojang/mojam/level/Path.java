package com.mojang.mojam.level;

import java.util.ArrayList;
import java.util.List;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.level.tile.Tile;
import com.mojang.mojam.math.Vec2;

public class Path {

	public static final Vec2 toWorld = new Vec2(Tile.WIDTH, Tile.HEIGHT);

	public boolean isFinished;
	List<Node> nodes = new ArrayList<Node>();
	int index = 0;

	public Path(boolean isFinished) {
		this.isFinished = isFinished;
	}

	public void addNodeFront(Node node) {
		nodes.add(0, node);
	}

	public boolean isDone() {
		return index >= nodes.size();
	}

	public Node getCurrent() {
		return nodes.get(index);
	}

	public void next() {
		++index;
	}

	public Vec2 getWorldPos(int i) {
		Vec2 wp = nodes.get(i).pos.mul(toWorld);
		wp.x += Tile.WIDTH * 0.5;
		wp.y += Tile.HEIGHT * 0.5;
		return wp;
	}

	public int size() {
		return nodes.size();
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public void merge(Path newPath) {
		int min = Math.min(Math.min(newPath.nodes.size(), nodes.size()), index);
		for (int i = 0; i < min; ++i)
			if (nodes.get(i).pos.equals(newPath.nodes.get(i).pos))
				newPath.next();
			else
				break;
	}

	public int getIndex() {
		return index;
	}
	
	public void render(){
	    for(Node n:nodes){
		MojamComponent.screen.alphaFill((int)n.pos.x, (int)n.pos.y, Tile.WIDTH, Tile.HEIGHT, 0x00ccff, 0x10);
	    }
	}

	public String toString() {
		String s = "";
		for (Node n : nodes) {
			s += n.pos.toString() + " ";
		}
		return s;
	}

	public Vec2 getCurrentWorldPos() {
		return getWorldPos(index);
	}

	public void previous() {
		index--;
	
		if (index <= 0) {
			index = 0;
		}
	}
}
