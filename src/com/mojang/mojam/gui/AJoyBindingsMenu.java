package com.mojang.mojam.gui;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

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

public class AJoyBindingsMenu extends GuiMenu {

	class JoyBindingButton extends Button {

		private final int MAX_LABEL_LENGTH = 13;

		public JoypadHandler jph;
		public String axisn;
		public Axis axis;
		private boolean selected = false;

		public JoyBindingButton(int id, String axisn, int x, int y) {
			super(id, null, x, y);
			this.setLabel(getMenuText(axisn));
			this.axisn = axisn;
		}

		public Axis getAxis() {
			return axis;
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
			
			if (selected) {
				JoypadHandler.askForAxis = instance;
			}
		}
		
		public void refresh() {
			this.setLabel(getMenuText(axisn));
		}
		
		public void updateAxis(Axis a) {
			String text = a.controller.getIndex()+":"+a.id+":"+a.name;
			updateAxis(text);
		}
		
		public void updateAxis(String text) {
			if (axisn.toUpperCase().equals("MOUSEX")) {
				JoypadHandler.mouseXA = text;
			}
			if (axisn.toUpperCase().equals("MOUSEY")) {
				JoypadHandler.mouseYA = text;
			}
			
			if (axisn.toUpperCase().equals("WALKX")) {
				JoypadHandler.walkXA = text;
			}
			if (axisn.toUpperCase().equals("WALKY")) {
				JoypadHandler.walkYA = text;
			}
			
			if (axisn.toUpperCase().equals("SHOOTX")) {
				JoypadHandler.shootXA = text;
			}
			if (axisn.toUpperCase().equals("SHOOTY")) {
				JoypadHandler.shootYA = text;
			}
			
			Options.set("joya_"+axisn+"A", text);
			
			setLabel(text);
		}
	}
	
	private static final int BORDER = 10;
	private static final int BUTTON_SPACING = 28;

	private int textWidth;
	private int yOffset;

	private ClickableComponent back;
	private JoyBindingButton selectedKey = null;
	
	private static AJoyBindingsMenu instance;
	
	private ArrayList<JoypadHandler.Button> joyButtonList = new ArrayList<JoypadHandler.Button>();
	
	public AJoyBindingsMenu() {
		super();
		
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

		
		addButton(new JoyBindingButton(TitleMenu.JOY_MOUSEX_ID, "mouseX", tab1, yOffset + 0
				* BUTTON_SPACING));
		addButton(new JoyBindingButton(TitleMenu.JOY_MOUSEY_ID, "mouseY", tab2, yOffset + 0
				* BUTTON_SPACING));
		
		addButton(new JoyBindingButton(TitleMenu.JOY_MOVEX_ID, "walkX", tab1, yOffset + 1
				* BUTTON_SPACING));
		addButton(new JoyBindingButton(TitleMenu.JOY_MOVEY_ID, "walkY", tab2, yOffset + 1
				* BUTTON_SPACING));
		
		addButton(new JoyBindingButton(TitleMenu.JOY_SHOOTX_ID, "shootX", tab1, yOffset + 2
				* BUTTON_SPACING));
		addButton(new JoyBindingButton(TitleMenu.JOY_SHOOTY_ID, "shootY", tab2, yOffset + 2
				* BUTTON_SPACING));
		
		
		
		back = addButton(new Button(TitleMenu.BACK_ID, MojamComponent.texts.getStatic("back"),
				(gameWidth - Button.BUTTON_WIDTH) / 2, yOffset + numRows * BUTTON_SPACING
						- Button.BUTTON_HEIGHT + 88));
		
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

	private String getMenuText(String name) {
		String axisn = "NONE";
		
		if (name.toUpperCase().equals("MOUSEX")) {
			axisn = JoypadHandler.mouseXA;
		}
		if (name.toUpperCase().equals("MOUSEY")) {
			axisn = JoypadHandler.mouseYA;
		}
		
		if (name.toUpperCase().equals("WALKX")) {
			axisn = JoypadHandler.walkXA;
		}
		if (name.toUpperCase().equals("WALKY")) {
			axisn = JoypadHandler.walkYA;
		}
		
		if (name.toUpperCase().equals("SHOOTX")) {
			axisn = JoypadHandler.shootXA;
		}
		if (name.toUpperCase().equals("SHOOTY")) {
			axisn = JoypadHandler.shootYA;
		}
		
		axisn = axisn.substring(axisn.lastIndexOf(":")+1);
		
		return axisn;
	}

	@Override
	public void render(AbstractScreen screen) {
		screen.blit(Art.background, 0, 0);
		Texts txts = MojamComponent.texts;
		Font.defaultFont().draw(screen, txts.getStatic("options.axeBindings"), MojamComponent.GAME_WIDTH / 2, yOffset - 40, Font.Align.CENTERED);
		write(screen, txts.getFormated("keys.mousea", "X"), 0, 0);
		write(screen, txts.getFormated("keys.mousea", "Y"), 1, 0);
		write(screen, txts.getFormated("keys.walka", "X"), 0, 1);
		write(screen, txts.getFormated("keys.walka", "Y"), 1, 1);
		write(screen, txts.getFormated("keys.shoota", "X"), 0, 2);
		write(screen, txts.getFormated("keys.shoota", "Y"), 1, 2);
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
		if (button == back || swapped) {
			return;
		}
		selectedKey = (JoyBindingButton) button;
		selectedKey.setSelected(true);
	}
	
	public void axisUsed(Axis a) {
		System.out.println(a.controller.getIndex()+":"+a.id);
		selectedKey.updateAxis(a);
		selectedKey.setSelected(false);
		selectedKey = null;
		refreshKeys();	
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		if (selectedKey != null) {
			if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				selectedKey.updateAxis("-1:NONE");
				Options.set("joya_"+selectedKey.axisn+"A", "");
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
			if(button instanceof JoyBindingButton) {
				((JoyBindingButton)button).refresh();
			}
		}
	}
	
	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyReleased(KeyEvent e) {}

}
