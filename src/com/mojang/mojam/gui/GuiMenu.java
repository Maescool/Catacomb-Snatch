package com.mojang.mojam.gui;

import java.awt.event.KeyListener;
import java.util.*;

import com.mojang.mojam.MouseButtons;
import com.mojang.mojam.screen.Screen;

public abstract class GuiMenu extends GuiComponent implements ButtonListener, KeyListener {

	protected List<ClickableComponent> buttons = new ArrayList<ClickableComponent>();

	protected ClickableComponent addButton(ClickableComponent button) {
		buttons.add(button);
		button.addListener(this);
		return button;
	}
	
    protected ClickableComponent removeButton(ClickableComponent button) {
        if (buttons.remove(button)) {
            return button;
        }
        else {
            return null;
        }
    }

	@Override
	public void render(Screen screen) {
		super.render(screen);

		for (ClickableComponent button : buttons) {
			button.render(screen);
		}
	}

	@Override
	public void tick(MouseButtons mouseButtons) {
		super.tick(mouseButtons);

		for (ClickableComponent button : buttons) {
			button.tick(mouseButtons);
		}
	}

	public void addButtonListener(ButtonListener listener) {
		for (ClickableComponent button : buttons) {
			button.addListener(listener);
		}
	}

}
