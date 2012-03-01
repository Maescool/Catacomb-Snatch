package com.mojang.mojam.gui;

import com.mojang.mojam.MouseButtons;
import com.mojang.mojam.screen.Screen;

public abstract class GuiComponent {

	public void render(Screen screen) {
	}

	public void tick(MouseButtons mouseButtons) {
	}

}
