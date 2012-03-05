package com.mojang.mojam.gui;

import com.mojang.mojam.MouseButtons;
import com.mojang.mojam.level.IEditable;
import com.mojang.mojam.screen.Bitmap;
import com.mojang.mojam.screen.Screen;

public class LevelEditorButton extends ClickableComponent {

    public static final int WIDTH = 42;
    public static final int HEIGHT = 56;
    private static final double scale = 1.2;

    private boolean isActive = false;
    private IEditable tile;
    private int id;
    
    // Background bitmaps for pressed/unpressed/inactive state
    private static final Bitmap background[] = new Bitmap[3];

    public LevelEditorButton(int id, IEditable tile, int x, int y) {
        super(x, y, WIDTH, HEIGHT);
        this.id = id;
        this.tile = tile;

        createBackground();
    }

   public IEditable getTile() {
	   return this.tile;
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

        // scale bitmap
        Bitmap smallBitmap = Bitmap.scaleBitmap(tile.getBitMapForEditor(),
                (int) (tile.getBitMapForEditor().w / scale), (int) (tile.getBitMapForEditor().h / scale));

        // render icon 
        screen.blit(smallBitmap, getX() + (getWidth() - smallBitmap.w) / 2, getY() + (getHeight() - smallBitmap.h) / 2);
    }

    @Override
    protected void clicked(MouseButtons mouseButtons) {
        isActive = true;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public int getId() {
        return this.id;
    }
}