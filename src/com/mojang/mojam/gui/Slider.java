package com.mojang.mojam.gui;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.MouseButtons;
import com.mojang.mojam.math.Mth;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Screen;

public class Slider extends ClickableComponent {
	private final int id;

	private String label;
	private boolean isDown;
	public float value = 1.0f;
	private int pos = getX();

	public static final int WIDTH = 128;
	public static final int HEIGHT = 24;
	public static final int SLIDER_WIDTH = 16;

	public Slider(int id, String label, int x, int y) {
		this(id, label, x, y, 1.0f);
	}

	public Slider(int id, String label, int x, int y, float value) {
		super(x, y, 128, 24);
		this.id = id;
		this.label = label;
		this.value = value;
		this.pos = (int) ((float) (getX() + getWidth() - SLIDER_WIDTH - getX()) * value) + getX();
	}

	@Override
	public void tick(MouseButtons mouseButtons) {
		super.tick(mouseButtons);

		int mx = mouseButtons.getX() / 2;
		int my = mouseButtons.getY() / 2;

		if (mx >= getX() && my >= getY() && mx < (getX() + getWidth())
				&& my < (getY() + getHeight())) {
			if (mouseButtons.isRelased(1)) {
				isDown = false;
			} else if (mouseButtons.isDown(1)) {
				isDown = true;

				pos = Mth.clamp((int) mx - (SLIDER_WIDTH / 2), getX(), getX() + getWidth()
						- SLIDER_WIDTH);
				float newValue = 1.0f / (float) (getX() + getWidth() - SLIDER_WIDTH - getX())
						* (float) (pos - getX());

				if (newValue != value) {
					value = newValue;
					performClick = true;
				}
			}
		}
	}

	@Override
	public void render(Screen screen) {
		screen.alphaFill(getX() + SLIDER_WIDTH, getY(), getWidth() - SLIDER_WIDTH * 2, getHeight(), 0xff000000, 0x80);
	    screen.alphaBlit(Art.slider[1][0], getX(), getY(), 0x80);
		screen.alphaBlit(Art.slider[1][1], getX() + getWidth() - SLIDER_WIDTH, getY(), 0x80);

		if (isDown)
			screen.blit(Art.slider[0][1], pos, getY());
		else
			screen.blit(Art.slider[0][0], pos, getY());

		String view = "";

		if (value == 0.0f)
			view = MojamComponent.texts.getStatic("options.mute");
		else
			view = (Math.round(value * 100.0f)) + "%";

		Font.defaultFont().draw(screen, label + ": " + view, getX() + getWidth() / 2, getY()
				+ getHeight() / 2, Font.Align.CENTERED);
	}

	public int getId() {
		return id;
	}

	public void setValue(float value) {
		this.value = value;
		pos = (int) ((float) (getX() + getWidth() - SLIDER_WIDTH - getX()) * value) + getX();
	}

	@Override
	protected void clicked(MouseButtons mouseButtons) {}
}
