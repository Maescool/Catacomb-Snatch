package com.mojang.mojam.gui;

import java.util.*;

import com.mojang.mojam.MouseButtons;
import com.mojang.mojam.screen.*;

public class Button extends GuiComponent {

    private List<ButtonListener> listeners;

    private boolean isPressed;

    private final int x;
    private final int y;
    private final int w;
    private final int h;

    private final int id;

    private int ix;

    private int iy;
    private boolean performClick = false;

    public Button(int id, int buttonImageIndex, int x, int y) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.w = 128;
        this.h = 24;
        this.ix = buttonImageIndex % 2;
        this.iy = buttonImageIndex / 2;
    }

    @Override
    public void tick(MouseButtons mouseButtons) {
        super.tick(mouseButtons);

        int mx = mouseButtons.getX() / 2;
        int my = mouseButtons.getY() / 2;
        isPressed = false;
        if (mx >= x && my >= y && mx < (x + w) && my < (y + h)) {
            if (mouseButtons.isRelased(1)) {
                postClick();
            } else if (mouseButtons.isDown(1)) {
                isPressed = true;
            }
        }

        if (performClick) {
            if (listeners != null) {
                for (ButtonListener listener : listeners) {
                    listener.buttonPressed(this);
                }
            }
            performClick = false;
        }
    }

    public void postClick() {
        performClick = true;
    }

    @Override
    public void render(Screen screen) {

        if (isPressed) {
            screen.blit(Art.buttons[ix][iy * 2 + 1], x, y);
        } else {
            screen.blit(Art.buttons[ix][iy * 2 + 0], x, y);
        }
    }

    public boolean isPressed() {
        return isPressed;
    }

    public void addListener(ButtonListener listener) {
        if (listeners == null) {
            listeners = new ArrayList<ButtonListener>();
        }
        listeners.add(listener);
    }

    public int getId() {
        return id;
    }
}
