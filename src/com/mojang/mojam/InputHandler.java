package com.mojang.mojam;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;

import com.mojang.mojam.Keys.Key;

public class InputHandler implements KeyListener {
	private Map<Integer, Key> mappings = new HashMap<Integer, Key>();

	public InputHandler(Keys keys) {
		//controls
		mappings.put(KeyEvent.VK_UP, keys.up);
		mappings.put(KeyEvent.VK_DOWN, keys.down);
		mappings.put(KeyEvent.VK_LEFT, keys.left);
		mappings.put(KeyEvent.VK_RIGHT, keys.right);

		mappings.put(KeyEvent.VK_NUMPAD8, keys.up);
		mappings.put(KeyEvent.VK_NUMPAD2, keys.down);
		mappings.put(KeyEvent.VK_NUMPAD4, keys.left);
		mappings.put(KeyEvent.VK_NUMPAD6, keys.right);

		mappings.put(KeyEvent.VK_W, keys.up);
		mappings.put(KeyEvent.VK_S, keys.down);
		mappings.put(KeyEvent.VK_A, keys.left);
		mappings.put(KeyEvent.VK_D, keys.right);

		//actions
		mappings.put(KeyEvent.VK_SPACE, keys.fire);
		mappings.put(KeyEvent.VK_ALT, keys.fire);
		mappings.put(KeyEvent.VK_CONTROL, keys.fire);
		mappings.put(KeyEvent.VK_SHIFT, keys.fire);
		mappings.put(KeyEvent.VK_C, keys.fire);

		mappings.put(KeyEvent.VK_X, keys.build);
		mappings.put(KeyEvent.VK_R, keys.build);
		mappings.put(KeyEvent.VK_Z, keys.use);
		mappings.put(KeyEvent.VK_E, keys.use);
		mappings.put(KeyEvent.VK_F, keys.upgrade);
		mappings.put(KeyEvent.VK_ESCAPE, keys.escape);
	}

	public void keyPressed(KeyEvent ke) {
		toggle(ke, true);
	}

	public void keyReleased(KeyEvent ke) {
		toggle(ke, false);
	}

	public void keyTyped(KeyEvent ke) {
	}

	private void toggle(KeyEvent ke, boolean state) {
		Key key = mappings.get(ke.getKeyCode());
		if (key != null) {
			key.nextState = state;
		}
	}
}
