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

	private Map<Integer, Key> mappings = new HashMap<Integer, Key>();

	public InputHandler(Keys keys) {
		// controls
		initKey(keys.up, KeyEvent.VK_W);
		initKey(keys.down, KeyEvent.VK_S);
		initKey(keys.left, KeyEvent.VK_A);
		initKey(keys.right, KeyEvent.VK_D);
		initKey(keys.sprint, KeyEvent.VK_SHIFT);

		// actions
		initKey(keys.fire, KeyEvent.VK_SPACE);
		initKey(keys.fireUp, KeyEvent.VK_UP);
		initKey(keys.fireDown, KeyEvent.VK_DOWN);
		initKey(keys.fireLeft, KeyEvent.VK_LEFT);
		initKey(keys.fireRight, KeyEvent.VK_RIGHT);
		initKey(keys.build, KeyEvent.VK_R);
		initKey(keys.use, KeyEvent.VK_E);
		initKey(keys.upgrade, KeyEvent.VK_F);
		initKey(keys.pause, KeyEvent.VK_ESCAPE);
		initKey(keys.screenShot, KeyEvent.VK_F2);
		initKey(keys.fullscreen, KeyEvent.VK_F11);
		initKey(keys.chat, KeyEvent.VK_T);
	}

	private void initKey(Key key, int defaultKeyCode) {
		int keyCode = defaultKeyCode;
		String property = Options.get(getKey(key));
		if (property != null) {
			try {
				keyCode = Integer.parseInt(property);
			} catch (NumberFormatException e) {
				// default key code will be used
			}
		}
		mappings.put(keyCode, key);
	}

	private String getKey(Key key) {
		return "key_" + key.name;
	}

	public void addMapping(Key key, int keyCode) {
		mappings.put(keyCode, key);
		Options.set(getKey(key), String.valueOf(keyCode));
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
