package com.mojang.mojam.gui;

import java.awt.event.KeyListener;
import java.util.*;

import com.mojang.mojam.MouseButtons;
import com.mojang.mojam.screen.Screen;

public abstract class GuiMenu extends GuiComponent implements ButtonListener, KeyListener {

    protected List<Button> buttons = new ArrayList<Button>();

    protected Button addButton(Button button) {
        buttons.add(button);
        button.addListener(this);
        return button;
    }

    @Override
    public void render(Screen screen) {
        super.render(screen);

        for (Button button : buttons) {
            button.render(screen);
        }
    }

    @Override
    public void tick(MouseButtons mouseButtons) {
        super.tick(mouseButtons);

        for (Button button : buttons) {
            button.tick(mouseButtons);
        }
    }

    public void addButtonListener(ButtonListener listener) {
        for (Button button : buttons) {
            button.addListener(listener);
        }
    }

    public void buttonPressed(Button button) {
    }

}
