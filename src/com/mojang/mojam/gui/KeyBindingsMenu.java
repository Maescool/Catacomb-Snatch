package com.mojang.mojam.gui;

import java.awt.event.KeyEvent;
import java.util.List;

import com.mojang.mojam.InputHandler;
import com.mojang.mojam.Keys;
import com.mojang.mojam.Keys.Key;
import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Screen;

public class KeyBindingsMenu extends GuiMenu {

	private int xOffset, yOffset;

	private ClickableComponent up, down, left, right, fire, build, use, upgrade, sprint;
	private ClickableComponent back;
	private SelectableButton selected = null;

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

		up = addButton(new SelectableButton(TitleMenu.KEY_UP_ID, getMenuText(keys.up),
				xOffset + 128, yOffset + 0 * 30));
		down = addButton(new SelectableButton(TitleMenu.KEY_DOWN_ID, getMenuText(keys.down),
				xOffset + 128, yOffset + 1 * 30));
		left = addButton(new SelectableButton(TitleMenu.KEY_LEFT_ID, getMenuText(keys.left),
				xOffset + 128, yOffset + 2 * 30));
		right = addButton(new SelectableButton(TitleMenu.KEY_RIGHT_ID, getMenuText(keys.right),
				xOffset + 128, yOffset + 3 * 30));
		sprint = addButton(new SelectableButton(TitleMenu.KEY_SPRINT_ID, getMenuText(keys.sprint),
				xOffset + 128, yOffset + 4 * 30));
		fire = addButton(new SelectableButton(TitleMenu.KEY_FIRE_ID, getMenuText(keys.fire),
				xOffset + 3 * 128, yOffset + 0 * 30));
		build = addButton(new SelectableButton(TitleMenu.KEY_BUILD_ID, getMenuText(keys.build),
				xOffset + 3 * 128, yOffset + 1 * 30));
		use = addButton(new SelectableButton(TitleMenu.KEY_USE_ID, getMenuText(keys.use),
				xOffset + 3 * 128, yOffset + 2 * 30));
		upgrade = addButton(new SelectableButton(TitleMenu.KEY_UPGRADE_ID,
				getMenuText(keys.upgrade), xOffset + 3 * 128, yOffset + 3 * 30));
		back = addButton(new Button(TitleMenu.BACK_ID, "back", (gameWidth - 128) / 2,
				yOffset + 6 * 30 - 24));
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
		int fontX = xOffset + 128;
		int fontY = this.yOffset + 8;
		Font.draw(screen, "up: ", fontX - Font.getStringWidth("up: "), fontY + 0 * 30);
		Font.draw(screen, "down: ", fontX - Font.getStringWidth("down: "), fontY + 1 * 30);
		Font.draw(screen, "left: ", fontX - Font.getStringWidth("left: "), fontY + 2 * 30);
		Font.draw(screen, "right: ", fontX - Font.getStringWidth("right: "), fontY + 3 * 30);
		Font.draw(screen, "sprint: ", fontX - Font.getStringWidth("sprint: "), fontY + 4 * 30);

		fontX = xOffset + 3 * 128;
		Font.draw(screen, "fire: ", fontX - Font.getStringWidth("fire: "), fontY + 0 * 30);
		Font.draw(screen, "build: ", fontX - Font.getStringWidth("build: "), fontY + 1 * 30);
		Font.draw(screen, "use: ", fontX - Font.getStringWidth("use: "), fontY + 2 * 30);
		Font.draw(screen, "upgrade: ", fontX - Font.getStringWidth("upgrade: "), fontY + 3 * 30);
		super.render(screen);
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
		selected = (SelectableButton) button;
		selected.setSelected(true);
	}

	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyPressed(KeyEvent e) {}

	@Override
	public void keyReleased(KeyEvent e) {
		if (selected != null) {
			if (selected == up) {
				set(keys.up, e.getKeyCode());
			} else if (selected == down) {
				set(keys.down, e.getKeyCode());
			} else if (selected == left) {
				set(keys.left, e.getKeyCode());
			} else if (selected == right) {
				set(keys.right, e.getKeyCode());
			} else if (selected == fire) {
				set(keys.fire, e.getKeyCode());
			} else if (selected == build) {
				set(keys.build, e.getKeyCode());
			} else if (selected == use) {
				set(keys.use, e.getKeyCode());
			} else if (selected == upgrade) {
				set(keys.upgrade, e.getKeyCode());
			} else if (selected == sprint) {
				set(keys.sprint, e.getKeyCode());
			}
			selected.setLabel(KeyEvent.getKeyText(e.getKeyCode()));
			selected.setSelected(false);
			selected = null;
		}
	}

	private void set(Key key, Integer keyCode) {
		inputHandler.clearMappings(key);
		inputHandler.addMapping(key, keyCode);
	}

}
