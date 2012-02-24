package com.mojang.mojam.gui;

import java.awt.event.KeyEvent;
import java.util.List;

import com.mojang.mojam.InputHandler;
import com.mojang.mojam.Keys;
import com.mojang.mojam.Keys.Key;
import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.resources.Texts;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Screen;

public class KeyBindingsMenu extends GuiMenu {

	private class KeyBindingButton extends Button {

		private Key key;
		private boolean selected = false;

		public KeyBindingButton(int id, Key key, int x, int y) {
			super(id, getMenuText(key), x, y);
			this.key = key;
		}

		public Key getKey() {
			return key;
		}

		@Override
		public String getLabel() {
			String label = super.getLabel();
			if (selected) {
				label = label.substring(1, label.length() - 1);
			}
			return label;
		}

		@Override
		public void setLabel(String label) {
			if (selected) {
				label = "-" + label + "-";
			}
			super.setLabel(label);
		}

		public boolean isSelected() {
			return selected;
		}

		public void setSelected(boolean selected) {
			String label = getLabel();
			this.selected = selected;
			setLabel(label);
		}
	}

	private int xOffset, yOffset;

	private ClickableComponent back;
	private KeyBindingButton selected = null;

	private Keys keys;
	private InputHandler inputHandler;

	public KeyBindingsMenu(Keys keys, InputHandler inputHandler) {
		super();
		this.keys = keys;
		this.inputHandler = inputHandler;
		addButtons();
	}

	private void addButtons() {
		int gameWidth = MojamComponent.GAME_WIDTH;
		int gameHeight = MojamComponent.GAME_HEIGHT;
		xOffset = (gameWidth - (4 * 128 + 40)) / 2;
		yOffset = (gameHeight - (6 * 30)) / 2;

		addButton(new KeyBindingButton(TitleMenu.KEY_UP_ID, keys.up, xOffset + 128,
				yOffset + 0 * 30));
		addButton(new KeyBindingButton(TitleMenu.KEY_DOWN_ID, keys.down, xOffset + 128,
				yOffset + 1 * 30));
		addButton(new KeyBindingButton(TitleMenu.KEY_LEFT_ID, keys.left, xOffset + 128,
				yOffset + 2 * 30));
		addButton(new KeyBindingButton(TitleMenu.KEY_RIGHT_ID, keys.right, xOffset + 128,
				yOffset + 3 * 30));
		addButton(new KeyBindingButton(TitleMenu.KEY_SPRINT_ID, keys.sprint, xOffset + 128,
				yOffset + 4 * 30));
		addButton(new KeyBindingButton(TitleMenu.KEY_FIRE_ID, keys.fire, xOffset + 3 * 128,
				yOffset + 0 * 30));
		addButton(new KeyBindingButton(TitleMenu.KEY_BUILD_ID, keys.build, xOffset + 3 * 128,
				yOffset + 1 * 30));
		addButton(new KeyBindingButton(TitleMenu.KEY_USE_ID, keys.use, xOffset + 3 * 128,
				yOffset + 2 * 30));
		addButton(new KeyBindingButton(TitleMenu.KEY_UPGRADE_ID, keys.upgrade, xOffset + 3 * 128,
				yOffset + 3 * 30));
		back = addButton(new Button(TitleMenu.BACK_ID, MojamComponent.texts.getStatic("back"), MojamComponent.GAME_WIDTH - 128 - 20, MojamComponent.GAME_HEIGHT - 24 - 25));
	}

	private String getMenuText(Key key) {
		List<Integer> mappings = inputHandler.getMappings(key);
		if (mappings.size() > 0) {
			return KeyEvent.getKeyText(mappings.get(0));
		} else {
			return "NONE";
		}
	}

	@Override
	public void render(Screen screen) {
		screen.blit(Art.background, 0, 0);
		Texts txts = MojamComponent.texts;
		write(screen, txts.getStatic("keys.up"), 1, 0);
		write(screen, txts.getStatic("keys.down"), 1, 1);
		write(screen, txts.getStatic("keys.left"), 1, 2);
		write(screen, txts.getStatic("keys.right"), 1, 3);
		write(screen, txts.getStatic("keys.sprint"), 1, 4);

		write(screen, txts.getStatic("keys.fire"), 3, 0);
		write(screen, txts.getStatic("keys.build"), 3, 1);
		write(screen, txts.getStatic("keys.use"), 3, 2);
		write(screen, txts.getStatic("keys.upgrade"), 3, 3);
		super.render(screen);
	}

	private void write(Screen screen, String txt, int column, int row) {
		Font.draw(screen, txt + ": ", xOffset + column * 128 - Font.getStringWidth(txt + ": "),
				yOffset + 8 + row * 30);
	}

	@Override
	public void buttonPressed(ClickableComponent button) {
		boolean swapped = selected == button;
		if (selected != null) {
			selected.setSelected(false);
			selected = null;
		}
		if (button == back || swapped) {
			return;
		}
		selected = (KeyBindingButton) button;
		selected.setSelected(true);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (selected != null) {
			inputHandler.clearMappings(selected.getKey());
			inputHandler.addMapping(selected.getKey(), e.getKeyCode());
			selected.setLabel(KeyEvent.getKeyText(e.getKeyCode()));
			selected.setSelected(false);
			selected = null;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyReleased(KeyEvent e) {}

}
