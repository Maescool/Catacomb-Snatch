package com.mojang.mojam;

import java.util.ArrayList;
import java.util.List;

public class Keys {
	public final class Key {
		public final String name;
		public boolean nextState = false;
		public boolean nextState2 = false;
		public boolean wasDown = false;
		public boolean isDown = false;
		public int keybTick = 0;

		public Key(String name) {
			this.name = name;
			all.add(this);
		}

		public void tick() {
			keybTick--;
			if (keybTick < 0) keybTick = 0;
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
	public Key console = new Key("console");
	
	public Key weaponSlot1 = new Key("weaponSlot1");
	public Key weaponSlot2 = new Key("weaponSlot2");
	public Key weaponSlot3 = new Key("weaponSlot3");
	public Key cycleLeft = new Key("cycleLeft");
	public Key cycleRight = new Key("cycleRight");
	
	public Key joy_click = new Key("joystickClick");
	
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
	
	public void addKey(Key k) {
		addKey(k, all.size());
	}
	
	public void addKey(Key k, int id) {
		all.add(id, k);
	}
	
	public void removeKey(int id) {
		removeKey(all.get(id));
	}
	
	public void removeKey(Key k) {
		all.remove(k);
	}
}
