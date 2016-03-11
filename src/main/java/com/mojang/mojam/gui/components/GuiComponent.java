package com.mojang.mojam.gui.components;

import com.mojang.mojam.MouseButtons;
import com.mojang.mojam.screen.AbstractScreen;

public abstract class GuiComponent {

	public void render(AbstractScreen screen) {
	}

	public void tick(MouseButtons mouseButtons) {
	}

}
