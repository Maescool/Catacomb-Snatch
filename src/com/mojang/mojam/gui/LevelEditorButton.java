package com.mojang.mojam.gui;

import com.mojang.mojam.MouseButtons;
import com.mojang.mojam.screen.Bitmap;
import com.mojang.mojam.screen.Screen;

public class LevelEditorButton extends ClickableComponent {

    public static final int WIDTH = 63;
    public static final int HEIGHT = 87;

    private int id;
    private String label;
    private Bitmap icon;
    
    private boolean isActive = false;
    
    // Background bitmaps for pressed/unpressed/inactive state
    private static final Bitmap background[] = new Bitmap[3];

    // Initialize background bitmaps
    static {
        background[0] = new Bitmap(WIDTH, HEIGHT);
        background[0].fill(0, 0, WIDTH, HEIGHT, 0xff522d16);
        background[0].fill(1, 1, WIDTH - 2, HEIGHT - 2, 0);
        background[1] = new Bitmap(WIDTH, HEIGHT);
        background[1].fill(0, 0, WIDTH, HEIGHT, 0xff26150a);
        background[1].fill(1, 1, WIDTH - 2, HEIGHT - 2, 0);
        background[2] = new Bitmap(WIDTH, HEIGHT);
        background[2].fill(0, 0, WIDTH, HEIGHT, 0xff26150a);
        background[2].fill(1, 1, WIDTH - 2, HEIGHT - 2, 0xff3a210f);
    }

    public LevelEditorButton(int id, Bitmap icon, String label, int x, int y) {
        super(x, y, WIDTH, HEIGHT);
        
        this.id = id;
        this.label = label;
        this.icon = icon;

        createBackground();
    }

    public int getId() {
        return id;
    }

    // Initialize background bitmaps
    private void createBackground() {
        background[0] = new Bitmap(WIDTH, HEIGHT);
        background[0].fill(0, 0, WIDTH, HEIGHT, 0xff522d16);
        background[0].fill(1, 1, WIDTH - 2, HEIGHT - 2, 0);
        background[1] = new Bitmap(WIDTH, HEIGHT);
        background[1].fill(0, 0, WIDTH, HEIGHT, 0xff26150a);
        background[1].fill(1, 1, WIDTH - 2, HEIGHT - 2, 0);
        background[2] = new Bitmap(WIDTH, HEIGHT);
        background[2].fill(0, 0, WIDTH, HEIGHT, 0xff26150a);
        background[2].fill(1, 1, WIDTH - 2, HEIGHT - 2, 0xff3a210f);
    }

    @Override
    public void render(Screen screen) {
        // render background
        screen.blit(background[isPressed() ? 1 : (isActive ? 2 : 0)], getX(), getY());

        // render icon        
        int height = icon.h + 17;
        screen.blit(icon, getX() + (getWidth() - icon.w) / 2, getY() + (getHeight() - height) / 2);

        // render label
        Font.defaultFont().drawCentered(screen, label, getX() + getWidth() / 2, (getY() + (getHeight() - height) / 2) + icon.h + 10);
    }

    @Override
    protected void clicked(MouseButtons mouseButtons) {
        isActive = true;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}