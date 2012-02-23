package com.mojang.mojam;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.mojang.mojam.Keys.Key;

public class InputHandler implements KeyListener {

	private Keys keys;
	private Map<Integer, Key> mappings = new HashMap<Integer, Key>();

	public InputHandler(Keys keys) {
		this.keys = keys;
		// controls
		mappings.put(Options.getAsInteger(Options.KEY_UP, KeyEvent.VK_W), keys.up);
		mappings.put(Options.getAsInteger(Options.KEY_DOWN, KeyEvent.VK_S), keys.down);
		mappings.put(Options.getAsInteger(Options.KEY_LEFT, KeyEvent.VK_A), keys.left);
		mappings.put(Options.getAsInteger(Options.KEY_RIGHT, KeyEvent.VK_D), keys.right);
		// actions
		mappings.put(Options.getAsInteger(Options.KEY_FIRE, KeyEvent.VK_SPACE), keys.fire);
		mappings.put(Options.getAsInteger(Options.KEY_BUILD, KeyEvent.VK_R), keys.build);
		mappings.put(Options.getAsInteger(Options.KEY_USE, KeyEvent.VK_E), keys.use);
		mappings.put(Options.getAsInteger(Options.KEY_UPGRADE, KeyEvent.VK_F), keys.upgrade);
		mappings.put(KeyEvent.VK_ESCAPE, keys.pause);
		mappings.put(KeyEvent.VK_F2, keys.screenShot);

		mappings.put(Options.getAsInteger(Options.KEY_SPRINT, KeyEvent.VK_SHIFT), keys.sprint);

		mappings.put(KeyEvent.VK_F11, keys.fullscreen);
	}

	public void addMapping(Key key, Integer keyCode) {
		mappings.put(keyCode, key);
		Options.set(getKey(key), keyCode);
	}

	private String getKey(Key key) {
		if (key == keys.up) {
			return Options.KEY_UP;
		} else if (key == keys.down) {
			return Options.KEY_DOWN;
		} else if (key == keys.left) {
			return Options.KEY_LEFT;
		} else if (key == keys.right) {
			return Options.KEY_RIGHT;
		} else if (key == keys.fire) {
			return Options.KEY_FIRE;
		} else if (key == keys.build) {
			return Options.KEY_BUILD;
		} else if (key == keys.use) {
			return Options.KEY_USE;
		} else if (key == keys.upgrade) {
			return Options.KEY_UPGRADE;
		} else if (key == keys.sprint) {
			return Options.KEY_SPRINT;
		}
		return null;
	}

	public void clearMappings(Key key) {
		for (Integer mapping : getMappings(key)) {
			mappings.remove(mapping);
		}
	}

	public List<Integer> getMappings(Key key) {
		ArrayList<Integer> keyCodes = new ArrayList<Integer>();
		for (Entry<Integer, Key> entry : mappings.entrySet()) {
			if (entry.getValue() == key) {
				keyCodes.add(entry.getKey());
			}
		}
		return keyCodes;
	}

	@Override
	public void keyPressed(KeyEvent ke) {
		toggle(ke, true);
	}

	@Override
	public void keyReleased(KeyEvent ke) {
		toggle(ke, false);
	}

	@Override
	public void keyTyped(KeyEvent ke) {}

	private void toggle(KeyEvent ke, boolean state) {
		Key key = mappings.get(ke.getKeyCode());
		if (key != null) {
			key.nextState = state;
		}
	}
}
