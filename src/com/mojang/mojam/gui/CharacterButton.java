package com.mojang.mojam.gui;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.screen.Bitmap;
import com.mojang.mojam.screen.Screen;

public class CharacterButton extends Button {

	public static final int WIDTH = 160;
	public static final int HEIGHT = 64;

	private static Bitmap backgrounds[] = new Bitmap[3];
	static {
		backgrounds[0] = new Bitmap(WIDTH, HEIGHT);
		backgrounds[0].fill(0, 0, WIDTH, HEIGHT, 0xff522d16);
		backgrounds[0].fill(1, 1, WIDTH - 2, HEIGHT - 2, 0);
		backgrounds[1] = new Bitmap(WIDTH, HEIGHT);
		backgrounds[1].fill(0, 0, WIDTH, HEIGHT, 0xff26150a);
		backgrounds[1].fill(1, 1, WIDTH - 2, HEIGHT - 2, 0);
		backgrounds[2] = new Bitmap(WIDTH, HEIGHT);
		backgrounds[2].fill(0, 0, WIDTH, HEIGHT, 0xff26150a);
		backgrounds[2].fill(1, 1, WIDTH - 2, HEIGHT - 2, 0xff3a210f);
	}

	private int characterID;
	private Bitmap characterArt;
	private boolean selected;
	private boolean hasFocus;

	public CharacterButton(int id, int characterID, Bitmap characterArt, int x, int y) {
		super(id, MojamComponent.texts.playerNameCharacter(characterID), x, y, WIDTH, HEIGHT);
		this.characterID = characterID;
		this.characterArt = characterArt;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public boolean isSelected() {
		return selected;
	}

	public int getCharacterID() {
		return characterID;
	}

	public void setFocus(boolean focus) {
		this.hasFocus = focus;
	}

	public boolean hasFocus() {
		return hasFocus;
	}

	@Override
	public void render(Screen screen) {
		screen.blit(backgrounds[isSelected() ? 2 : (isPressed() || hasFocus ? 1 : 0)], getX(),
				getY());
		screen.blit(characterArt, getX() + (WIDTH - characterArt.w) / 2, getY() + 8);
		Font.defaultFont().draw(screen, getLabel(), getX() + WIDTH / 2, getY() + HEIGHT - 12, Font.Align.CENTERED);
	}
}
