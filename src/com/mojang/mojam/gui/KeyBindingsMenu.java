package com.mojang.mojam.gui;

import java.awt.event.KeyEvent;

import com.mojang.mojam.InputHandler;
import com.mojang.mojam.Keys;
import com.mojang.mojam.Keys.Key;
import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.resources.Texts;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Screen;

public class KeyBindingsMenu extends GuiMenu {

	class KeyBindingButton extends Button {

		private final int MAX_LABEL_LENGTH = 13;

		private Key key;
		private boolean selected = false;

		public KeyBindingButton(int id, Key key, int x, int y) {
			super(id, null, x, y);
			this.setLabel(getMenuText(key));
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
				super.setLabel("-" + trimToFitButton(label) + "-");
			} else {
				super.setLabel(trimToFitButton(label));
			}
		}

		public String trimToFitButton(String label) {
			if (label.length() > MAX_LABEL_LENGTH) {
				return label.substring(0, MAX_LABEL_LENGTH - 2) + "...";
			} else {
				return label;
			}
		}

		public boolean isSelected() {
			return selected;
		}

		public void setSelected(boolean selected) {
			String label = getLabel();
			this.selected = selected;
			setLabel(label);
		}
		
		public void refresh() {
			this.setLabel(getMenuText(key));
		}
	}

	private static final int BORDER = 10;
	private static final int BUTTON_SPACING = 32;

	private int textWidth;
	private int yOffset;

	private ClickableComponent back;
	private KeyBindingButton selectedKey = null;

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
		textWidth = (gameWidth - 2 * BORDER - 2 * 32 - 2 * Button.BUTTON_WIDTH) / 2;
		int numRows = 6;
		int tab1 = BORDER + 32 + textWidth;
		int tab2 = gameWidth - BORDER - Button.BUTTON_WIDTH;
		yOffset = (gameHeight - (numRows * BUTTON_SPACING + 32)) / 2;

		addButton(new KeyBindingButton(TitleMenu.KEY_UP_ID, keys.up, tab1, yOffset + 0
				* BUTTON_SPACING));
		addButton(new KeyBindingButton(TitleMenu.KEY_DOWN_ID, keys.down, tab1, yOffset + 1
				* BUTTON_SPACING));
		addButton(new KeyBindingButton(TitleMenu.KEY_LEFT_ID, keys.left, tab1, yOffset + 2
				* BUTTON_SPACING));
		addButton(new KeyBindingButton(TitleMenu.KEY_RIGHT_ID, keys.right, tab1, yOffset + 3
				* BUTTON_SPACING));
		addButton(new KeyBindingButton(TitleMenu.KEY_SPRINT_ID, keys.sprint, tab1, yOffset + 4
				* BUTTON_SPACING));
		addButton(new KeyBindingButton(TitleMenu.KEY_FIRE_ID, keys.fire, tab1, yOffset + 5
				* BUTTON_SPACING));
		addButton(new KeyBindingButton(TitleMenu.KEY_FIRE_UP_ID, keys.fireUp, tab2, yOffset + 0
				* BUTTON_SPACING));
		addButton(new KeyBindingButton(TitleMenu.KEY_FIRE_DOWN_ID, keys.fireDown, tab2, yOffset + 1
				* BUTTON_SPACING));
		addButton(new KeyBindingButton(TitleMenu.KEY_FIRE_LEFT_ID, keys.fireLeft, tab2, yOffset + 2
				* BUTTON_SPACING));
		addButton(new KeyBindingButton(TitleMenu.KEY_FIRE_RIGHT_ID, keys.fireRight, tab2, yOffset + 3
				* BUTTON_SPACING));
		addButton(new KeyBindingButton(TitleMenu.KEY_BUILD_ID, keys.build, tab2, yOffset + 4
				* BUTTON_SPACING));
		addButton(new KeyBindingButton(TitleMenu.KEY_USE_ID, keys.use, tab2, yOffset + 5
				* BUTTON_SPACING));
		addButton(new KeyBindingButton(TitleMenu.KEY_UPGRADE_ID, keys.upgrade, tab2, yOffset + 6
				* BUTTON_SPACING));
		addButton(new KeyBindingButton(TitleMenu.KEY_CHAT_ID, keys.chat, tab2, yOffset + 7
				* BUTTON_SPACING));
		back = addButton(new Button(TitleMenu.BACK_ID, MojamComponent.texts.getStatic("back"),
				(gameWidth - Button.BUTTON_WIDTH) / 2, yOffset + numRows * BUTTON_SPACING
						- Button.BUTTON_HEIGHT + 88));
	}

	private String getMenuText(Key key) {
		Integer keyEvent = inputHandler.getKeyEvent(key);
		if (keyEvent != null && keyEvent != KeyEvent.VK_UNDEFINED) {
			return KeyEvent.getKeyText(keyEvent);
		}
		// TODO put text in translation file
		return "NONE";
	}

	@Override
	public void render(Screen screen) {
		screen.blit(Art.background, 0, 0);
		Texts txts = MojamComponent.texts;
		Font.defaultFont().draw(screen, txts.getStatic("options.keyBindings"), MojamComponent.GAME_WIDTH / 2, yOffset - 40, Font.Align.CENTERED);
		write(screen, txts.getStatic("keys.up"), 0, 0);
		write(screen, txts.getStatic("keys.down"), 0, 1);
		write(screen, txts.getStatic("keys.left"), 0, 2);
		write(screen, txts.getStatic("keys.right"), 0, 3);
		write(screen, txts.getStatic("keys.sprint"), 0, 4);
		write(screen, txts.getStatic("keys.fire"), 0, 5);

		write(screen, txts.getStatic("keys.fireUp"), 1, 0);
		write(screen, txts.getStatic("keys.fireDown"), 1, 1);
		write(screen, txts.getStatic("keys.fireLeft"), 1, 2);
		write(screen, txts.getStatic("keys.fireRight"), 1, 3);
		write(screen, txts.getStatic("keys.build"), 1, 4);
		write(screen, txts.getStatic("keys.use"), 1, 5);
		write(screen, txts.getStatic("keys.upgrade"), 1, 6);
		write(screen, txts.getStatic("keys.chat"), 1, 7);
		super.render(screen);
		ClickableComponent button = buttons.get(selectedItem);
		if (button == back) {
			screen.blit(Art.getLocalPlayerArt()[0][6], back.getX() - 64, back.getY() - 8);
		} else {
			screen.blit(Art.getLocalPlayerArt()[0][6], button.getX() - textWidth - 32,
					button.getY() - 8);
		}
	}

	private void write(Screen screen, String txt, int column, int row) {
		Font.defaultFont().draw(screen, txt + ": ", BORDER + 32 + textWidth + column
				* (Button.BUTTON_WIDTH + 32 + textWidth), yOffset
				+ 8 + row * BUTTON_SPACING, Font.Align.RIGHT);
	}

	@Override
	public void buttonPressed(ClickableComponent button) {
		boolean swapped = selectedKey == button;
		if (selectedKey != null) {
			selectedKey.setSelected(false);
			selectedKey = null;
		}
		if (button == back || swapped) {
			return;
		}
		selectedKey = (KeyBindingButton) button;
		selectedKey.setSelected(true);
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		if (selectedKey != null) {
			inputHandler.addMapping(selectedKey.getKey(), e.getKeyCode());
			selectedKey.setLabel(KeyEvent.getKeyText(e.getKeyCode()));
			selectedKey.setSelected(false);
			selectedKey = null;
			refreshKeys();	
		} else {			
			if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {
				if (buttons.get(selectedItem) == back) {
					selectedItem -= 6;
				} else if (selectedItem >= 5) {
					selectedItem -= 5;
				}
			} else if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {
				if (selectedItem < 5) {
					selectedItem += 5;
				} else if (buttons.get(selectedItem) == back) {
					selectedItem--;
				}
			} else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				back.postClick();
			} else {
				super.keyPressed(e);
			}
		}
	}
	
	public void refreshKeys() {
		for(ClickableComponent button : super.buttons) {
			if(button instanceof KeyBindingButton)
				((KeyBindingButton)button).refresh();
		}
	}
	
	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyReleased(KeyEvent e) {}

}
