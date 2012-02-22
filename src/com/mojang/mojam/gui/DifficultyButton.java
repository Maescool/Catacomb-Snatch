package com.mojang.mojam.gui;

import com.mojang.mojam.MouseButtons;
import com.mojang.mojam.screen.Bitmap;
import com.mojang.mojam.screen.Screen;

public class DifficultyButton  extends ClickableComponent {
	
	private int id;
	private String Diffname;
	
	public static final int WIDTH = 140;
	public static final int HEIGHT = 19;

	public DifficultyButton(int id, String Diffname, int x, int y) {
		super(x, y, WIDTH, HEIGHT);
		this.id = id;
		this.Diffname = Diffname;
	}

	public int getId() {
		return id;
	}
	
	private static Bitmap background[] = new Bitmap[3];
	static {
		background[0] = new Bitmap(WIDTH, HEIGHT);
		background[0].fill(0, 0, WIDTH, HEIGHT, 0xff522d16);
		background[0].fill(1, 1, WIDTH-2, HEIGHT-2, 0);
		background[1] = new Bitmap(WIDTH, HEIGHT);
		background[1].fill(0, 0, WIDTH, HEIGHT, 0xff26150a);
		background[1].fill(1, 1, WIDTH-2, HEIGHT-2, 0);
		background[2] = new Bitmap(WIDTH, HEIGHT);
		background[2].fill(0, 0, WIDTH, HEIGHT, 0xff26150a);
		background[2].fill(1, 1, WIDTH-2, HEIGHT-2, 0xff3a210f);
	}
	
	@Override
	public void render(Screen screen) {
		screen.blit(background[isPressed() ? 1 : (isActive ? 2 : 0)], getX(), getY());
		Font.drawCentered(screen, Diffname, getX() + getWidth() / 2, getY() + 10);
	}
	
		@Override
		protected void clicked(MouseButtons mouseButtons) {
			isActive = true;
		}

		private boolean isActive = false;

		public void setActive(boolean active) {
			isActive = active;
		}
}
