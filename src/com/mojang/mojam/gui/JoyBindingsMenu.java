package com.mojang.mojam.gui;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

import org.lwjgl.input.Controller;

import com.mojang.mojam.InputHandler;
import com.mojang.mojam.JoypadHandler;
import com.mojang.mojam.Options;
import com.mojang.mojam.JoypadHandler.Axis;
import com.mojang.mojam.Keys;
import com.mojang.mojam.Keys.Key;
import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.gui.components.Button;
import com.mojang.mojam.gui.components.ButtonListener;
import com.mojang.mojam.gui.components.ClickableComponent;
import com.mojang.mojam.gui.components.Font;
import com.mojang.mojam.resources.Texts;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.AbstractScreen;

public class JoyBindingsMenu extends GuiMenu {

	class JoyBindingButton extends Button {

		private final int MAX_LABEL_LENGTH = 13;

		private Key key;
		public JoypadHandler jph;
		public JoypadHandler.Button button;
		private boolean selected = false;

		public JoyBindingButton(int id, Key key, int x, int y) {
			super(id, null, x, y);
			this.setLabel(getMenuText(key));
			this.key = key;
			this.button = getJoyButton(key);
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
			
			if (selected) JoypadHandler.askForButton = instance;
		}
		
		public void refresh() {
			this.setLabel(getMenuText(key));
		}
	}

	private static final int BORDER = 10;
	private static final int BUTTON_SPACING = 28;

	private int textWidth;
	private int yOffset;

	private ClickableComponent axes;
	
	private ClickableComponent back;
	private JoyBindingButton selectedKey = null;

	private Keys keys;
	private InputHandler inputHandler;
	
	private static JoyBindingsMenu instance;
	
	private ArrayList<JoypadHandler.Button> joyButtonList = new ArrayList<JoypadHandler.Button>();
	
	public JoyBindingsMenu(Keys keys, InputHandler inputHandler) {
		super();
		this.keys = keys;
		this.inputHandler = inputHandler;
		
		for (JoypadHandler handler : JoypadHandler.handlers) {
			for (Object o : handler.butaxes) {
				if (o instanceof JoypadHandler.Button) {
					JoypadHandler.Button b = (JoypadHandler.Button) o;
					joyButtonList.add(b);
				}
			}
		}
		
		addButtons();
		
		instance = this;
		
	}

	private void addButtons() {
		int gameWidth = MojamComponent.GAME_WIDTH;
		int gameHeight = MojamComponent.GAME_HEIGHT;
		textWidth = (gameWidth - 2 * BORDER - 2 * 32 - 2 * Button.BUTTON_WIDTH) / 2;
		int numRows = 8;
		int tab1 = BORDER + 32 + textWidth;
		int tab2 = gameWidth - BORDER - Button.BUTTON_WIDTH;
		yOffset = (gameHeight - (numRows * BUTTON_SPACING + 32)) / 2;

		addButton(new JoyBindingButton(TitleMenu.JOY_UP_ID, keys.up, tab1, yOffset + 0
				* BUTTON_SPACING));
		addButton(new JoyBindingButton(TitleMenu.JOY_DOWN_ID, keys.down, tab1, yOffset + 1
				* BUTTON_SPACING));
		addButton(new JoyBindingButton(TitleMenu.JOY_LEFT_ID, keys.left, tab1, yOffset + 2
				* BUTTON_SPACING));
		addButton(new JoyBindingButton(TitleMenu.JOY_RIGHT_ID, keys.right, tab1, yOffset + 3
				* BUTTON_SPACING));
		addButton(new JoyBindingButton(TitleMenu.JOY_SPRINT_ID, keys.sprint, tab1, yOffset + 4
				* BUTTON_SPACING));
		addButton(new JoyBindingButton(TitleMenu.JOY_FIRE_ID, keys.fire, tab1, yOffset + 5
				* BUTTON_SPACING));
		addButton(new JoyBindingButton(TitleMenu.JOY_WEAPON_SLOT_1_ID, keys.weaponSlot1, tab1, yOffset + 6
				* BUTTON_SPACING));
		addButton(new JoyBindingButton(TitleMenu.JOY_WEAPON_SLOT_2_ID, keys.weaponSlot2, tab1, yOffset + 7
				* BUTTON_SPACING));
		addButton(new JoyBindingButton(TitleMenu.JOY_WEAPON_SLOT_3_ID, keys.weaponSlot3, tab1, yOffset + 8
				* BUTTON_SPACING));	
		addButton(new JoyBindingButton(TitleMenu.JOY_CLICK_ID, keys.joy_click, tab1, yOffset + 9
				* BUTTON_SPACING));	
				
		addButton(new JoyBindingButton(TitleMenu.JOY_FIRE_UP_ID, keys.fireUp, tab2, yOffset + 0
				* BUTTON_SPACING));
		addButton(new JoyBindingButton(TitleMenu.JOY_FIRE_DOWN_ID, keys.fireDown, tab2, yOffset + 1
				* BUTTON_SPACING));
		addButton(new JoyBindingButton(TitleMenu.JOY_FIRE_LEFT_ID, keys.fireLeft, tab2, yOffset + 2
				* BUTTON_SPACING));
		addButton(new JoyBindingButton(TitleMenu.JOY_FIRE_RIGHT_ID, keys.fireRight, tab2, yOffset + 3
				* BUTTON_SPACING));
		addButton(new JoyBindingButton(TitleMenu.JOY_BUILD_ID, keys.build, tab2, yOffset + 4
				* BUTTON_SPACING));
		addButton(new JoyBindingButton(TitleMenu.JOY_USE_ID, keys.use, tab2, yOffset + 5
				* BUTTON_SPACING));
		addButton(new JoyBindingButton(TitleMenu.JOY_UPGRADE_ID, keys.upgrade, tab2, yOffset + 6
				* BUTTON_SPACING));
		addButton(new JoyBindingButton(TitleMenu.JOY_CYCLE_LEFT_ID, keys.cycleLeft, tab2, yOffset + 7
				* BUTTON_SPACING));
		addButton(new JoyBindingButton(TitleMenu.JOY_CYCLE_RIGHT_ID, keys.cycleRight, tab2, yOffset + 8
				* BUTTON_SPACING));
		addButton(new JoyBindingButton(TitleMenu.JOY_PAUSE_ID, keys.pause, tab2, yOffset + 9
				* BUTTON_SPACING));
		

		
		axes = addButton(new Button(TitleMenu.AXES_MENU, MojamComponent.texts.getStatic("options.axeBindings"),
				gameWidth - Button.BUTTON_WIDTH, yOffset + numRows * BUTTON_SPACING
						- Button.BUTTON_HEIGHT + 88));
		
		back = addButton(new Button(TitleMenu.BACK_ID, MojamComponent.texts.getStatic("back"),
				(gameWidth - Button.BUTTON_WIDTH) / 2, yOffset + numRows * BUTTON_SPACING
						- Button.BUTTON_HEIGHT + 88));
		
		axes.addListener(new ButtonListener() {

			@Override
			public void buttonPressed(ClickableComponent button) {
				JoypadHandler.askForButton = null;
				JoypadHandler.askForAxis = null;
			}

			@Override
			public void buttonHovered(ClickableComponent clickableComponent) {
			}
			
		});
		
		back.addListener(new ButtonListener() {

			@Override
			public void buttonPressed(ClickableComponent button) {
				JoypadHandler.askForButton = null;
				JoypadHandler.askForAxis = null;
			}

			@Override
			public void buttonHovered(ClickableComponent clickableComponent) {
			}
			
		});
	}

	private String getMenuText(Key key) {
		String joyButton = getJoyButton(key).name;
		
		return joyButton;
	}


	
	private JoypadHandler.Button getJoyButton(Key key) {
		for (JoypadHandler.Button b : joyButtonList) {
			if (b.simulKey == key) return b;
		}
		return new JoypadHandler.Button("NONE", new JoypadHandler().new DummyController(), -1);
	}

	@Override
	public void render(AbstractScreen screen) {
		screen.blit(Art.background, 0, 0);
		Texts txts = MojamComponent.texts;
		Font.defaultFont().draw(screen, txts.getStatic("options.joyBindings"), MojamComponent.GAME_WIDTH / 2, yOffset - 40, Font.Align.CENTERED);
		write(screen, txts.getStatic("keys.up"), 0, 0);
		write(screen, txts.getStatic("keys.down"), 0, 1);
		write(screen, txts.getStatic("keys.left"), 0, 2);
		write(screen, txts.getStatic("keys.right"), 0, 3);
		write(screen, txts.getStatic("keys.sprint"), 0, 4);
		write(screen, txts.getStatic("keys.fire"), 0, 5);
		write(screen, txts.keyWeaponSlot(1), 0, 6);
		write(screen, txts.keyWeaponSlot(2), 0, 7);
		write(screen, txts.keyWeaponSlot(3), 0, 8);
		write(screen, txts.getStatic("keys.click"), 0, 9);

		write(screen, txts.getStatic("keys.fireUp"), 1, 0);
		write(screen, txts.getStatic("keys.fireDown"), 1, 1);
		write(screen, txts.getStatic("keys.fireLeft"), 1, 2);
		write(screen, txts.getStatic("keys.fireRight"), 1, 3);
		write(screen, txts.getStatic("keys.build"), 1, 4);
		write(screen, txts.getStatic("keys.use"), 1, 5);
		write(screen, txts.getStatic("keys.upgrade"), 1, 6);
		write(screen, txts.getStatic("keys.cycleLeft"), 1, 7);
		write(screen, txts.getStatic("keys.cycleRight"), 1, 8);
		write(screen, txts.getStatic("keys.pause"), 1, 9);
		super.render(screen);
		ClickableComponent button = buttons.get(selectedItem);
		if (button == back) {
			screen.blit(Art.getLocalPlayerArt()[0][6], back.getX() - 64, back.getY() - 8);
		} else {
			screen.blit(Art.getLocalPlayerArt()[0][6], button.getX() - textWidth - 32,
					button.getY() - 8);
		}
	}

	private void write(AbstractScreen screen, String txt, int column, int row) {
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
		if (button == back || button == axes || swapped) {
			return;
		}
		selectedKey = (JoyBindingButton) button;
		selectedKey.setSelected(true);
	}
	
	public void joyPressed(JoypadHandler.Button b) {
		if (selectedKey == null) return;
		if (b == null) return;
		b.simulKey = selectedKey.getKey();
		Options.set("joyb_"+b.controller.getIndex()+"_"+b.id, b.simulKey.name);
		selectedKey.setLabel(b.name);
		selectedKey.setSelected(false);
		selectedKey = null;
		refreshKeys();	
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		if (selectedKey != null) {
			if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				JoypadHandler.Button b = getJoyButton(selectedKey.getKey());
				b.simulKey = null;
				Options.set("joyb_"+b.controller.getIndex()+"_"+b.id, "");
				selectedKey.setLabel("NONE");
				selectedKey.setSelected(false);
				selectedKey = null;
				refreshKeys();	
			}
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
			if(button instanceof JoyBindingButton)
				((JoyBindingButton)button).refresh();
		}
	}
	
	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyReleased(KeyEvent e) {}

}
