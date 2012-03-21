package com.mojang.mojam.gui;

import com.mojang.mojam.GameCharacter;
import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.gui.components.Button;
import com.mojang.mojam.gui.components.Font;
import com.mojang.mojam.screen.AbstractBitmap;
import com.mojang.mojam.screen.AbstractScreen;
import com.mojang.mojam.screen.Art;

public class CharacterButton extends Button {

	public static final int WIDTH = 160;
	public static final int HEIGHT = 64;
	private GameCharacter character;
	private AbstractBitmap characterArt;
	private boolean selected;
	private boolean hasFocus;

	public CharacterButton(int id, GameCharacter character, AbstractBitmap characterArt, int x, int y) {
		super(id, MojamComponent.texts.playerNameCharacter(character), x, y, WIDTH, HEIGHT);
		this.character = character;
		this.characterArt = characterArt;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public boolean isSelected() {
		return selected;
	}

	public GameCharacter getCharacter() {
		return character;
	}

	public void setFocus(boolean focus) {
		this.hasFocus = focus;
	}

	public boolean hasFocus() {
		return hasFocus;
	}

	@Override
	public void render(AbstractScreen screen) {
		screen.blit(Art.backCharacterButton[isSelected() ? 2 : (isPressed() || hasFocus ? 1 : 0)], getX(),
			getY());
		screen.blit(characterArt, getX() + (WIDTH - characterArt.getWidth()) / 2, getY() + 8);
		Font.defaultFont().draw(screen, getLabel(), getX() + WIDTH / 2, getY() + HEIGHT - 12, Font.Align.CENTERED);
	}
}
