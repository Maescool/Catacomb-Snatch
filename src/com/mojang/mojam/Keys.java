package com.mojang.mojam;

import java.util.ArrayList;
import java.util.List;

public class Keys {
	public final class Key {
		public final String name;
		public boolean nextState = false;
		public boolean wasDown = false;
		public boolean isDown = false;

		public Key(String name) {
			this.name = name;
			all.add(this);
		}

		public void tick() {
			wasDown = isDown;
			isDown = nextState;
		}

		public boolean wasPressed() {
			return !wasDown && isDown;
		}

		public boolean wasReleased() {
			return wasDown && !isDown;
		}

		public void release() {
			nextState = false;
		}
	}

	private List<Key> all = new ArrayList<Key>();

	public Key up = new Key("up");
	public Key down = new Key("down");
	public Key left = new Key("left");
	public Key right = new Key("right");
	public Key fire = new Key("fire");
    public Key fireUp = new Key("fireUp");
    public Key fireDown = new Key("fireDown");
    public Key fireLeft = new Key("fireLeft");
    public Key fireRight = new Key("fireRight");
	public Key build = new Key("build");
	public Key use = new Key("use");
	public Key upgrade = new Key("upgrade");
	public Key pause = new Key("pause");
	public Key fullscreen = new Key("fullscreen");
	public Key sprint = new Key("sprint");
	public Key screenShot = new Key("screenShot");
	public Key chat = new Key("chat");

	public void tick() {
		for (Key key : all)
			key.tick();
	}

	public void release() {
		for (Key key : all)
			key.release();
	}

	public List<Key> getAll() {
		return all;
	}
}
