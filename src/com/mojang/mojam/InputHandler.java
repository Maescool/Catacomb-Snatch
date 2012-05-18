package com.mojang.mojam;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.mojang.mojam.Keys.Key;
import com.mojang.mojam.mod.ModSystem;

public class InputHandler implements KeyListener {

	private Map<Key, Integer> mappings = new HashMap<Key, Integer>();

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
		initKey(keys.weaponSlot1, KeyEvent.VK_1);
		initKey(keys.weaponSlot2, KeyEvent.VK_2);
		initKey(keys.weaponSlot3, KeyEvent.VK_3);
		initKey(keys.cycleLeft, KeyEvent.VK_Z);
		initKey(keys.cycleRight, KeyEvent.VK_X);
		
		//console
		initKey(keys.console, KeyEvent.VK_BACK_QUOTE);
		
		//joypad specials
		initKey(keys.joy_click, -1);
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
		mappings.put(key, keyCode);
	}

	private String getKey(Key key) {
		return "key_" + key.name;
	}

	public void addMapping(Key key, int keyCode) {
		// make sure no key is bound to more than one event
		clearMappings(keyCode);
		mappings.put(key, keyCode);
		Options.set(getKey(key), String.valueOf(keyCode));
	}

	private void clearMappings(int keyCode) {
		Set<Key> keySet = mappings.keySet();
		for (Key _key : keySet) {
			if (mappings.get(_key) == keyCode) {
				mappings.put(_key, KeyEvent.VK_UNDEFINED);
				Options.set(getKey(_key), String.valueOf(keyCode));
			}
		}
	}
	
	public Integer getKeyEvent(Key key) {
		return mappings.get(key);
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
		Key key = null;
		Set<Key> keySet = mappings.keySet();
		for (Key _key : keySet) {
			if (mappings.get(_key) == ke.getKeyCode())
				key = _key;
		}
		if (key != null) {
			key.keybTick = 9;
			key.nextState = state;
			key.nextState2 = state;
			ModSystem.keyEvent(key, state);
		}
	}

	public void toggleJoypad(Key key, boolean state) {
		//if (key.keybTick > 2) {
		//	key.nextJoyState = false;
		//	return;
		//}
		//if (key.nextState && key.keybTick > 2) return;
		
		if (key.name == MojamComponent.instance.keys.joy_click.name) {
			MouseButtons mb = MojamComponent.instance.mouseButtons;
			MojamComponent.instance.mouseButtons.nextState[1] = (!mb.nextState2[1])?state:mb.nextState[1];
			return;
		}
		
		key.nextState = (!key.nextState2)?state:key.nextState;
	}
}
