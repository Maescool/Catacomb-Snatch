package com.mojang.mojam.gui.components;

import com.mojang.mojam.MouseButtons;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.AbstractScreen;
import com.mojang.mojam.screen.MojamBitmap;

public class Checkbox extends ClickableComponent {
	private final int id;

	private String label;
	public boolean checked = false;

	public static final int WIDTH = 140;
	public static final int HEIGHT = 19;

	public Checkbox(int id, String label, int x, int y) {
		this(id, label, x, y, false);
	}

	public int getId() {
		return id;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Checkbox(int id, String label, int x, int y, boolean checked) {
		super(x, y, 128, 24);
		this.id = id;
		this.label = label;
		this.checked = checked;
	}

	protected void clicked(MouseButtons mouseButtons) {
		checked = !checked;
	}

	public void render(AbstractScreen screen) {
		if (isPressed()) {
			if (checked)
				screen.blit(Art.checkbox[1][1], getX(), getY());
			else
				screen.blit(Art.checkbox[0][1], getX(), getY());
		} else {
			if (checked)
				screen.blit(Art.checkbox[1][0], getX(), getY());
			else
				screen.blit(Art.checkbox[0][0], getX(), getY());
		}

		Font.defaultFont().draw(screen, label, getX() + 24 + 4,
				getY() + getHeight() / 2 - 4);
	}
	
	public MojamBitmap getBitmap(int state) { 
		MojamBitmap b = new MojamBitmap(getWidth(), getHeight());
		if (state == 0 || state == 1) {
			if (state == 1)
				b.blit(Art.checkbox[1][1], 0, 0);
			else
				b.blit(Art.checkbox[0][1], 0, 0);
		} else {
			if (state == 2)
				b.blit(Art.checkbox[1][0], 0, 0);
			else
				b.blit(Art.checkbox[0][0], 0, 0);
		}
		
		Font.defaultFont().drawB(b, label, + 24 + 4,
				getHeight() / 2 - 4);
		
		return b;
	}

	public MojamBitmap[] getBitmaps() {
		MojamBitmap[] bmps = new MojamBitmap[4];
		bmps[0] = getBitmap(0);
		bmps[1] = getBitmap(1);
		bmps[2] = getBitmap(2);
		bmps[3] = getBitmap(3);
		return bmps;
	}
}
