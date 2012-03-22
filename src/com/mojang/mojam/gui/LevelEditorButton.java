package com.mojang.mojam.gui;

import com.mojang.mojam.MouseButtons;
import com.mojang.mojam.gui.components.ClickableComponent;
import com.mojang.mojam.level.IEditable;
import com.mojang.mojam.screen.AbstractBitmap;
import com.mojang.mojam.screen.AbstractScreen;
import com.mojang.mojam.screen.Art;

public class LevelEditorButton extends ClickableComponent {

    public static final int WIDTH = 42;
    public static final int HEIGHT = 56;
    private static final double scale = 1.2;

    private boolean isActive = false;
    private IEditable tile;
    private int id;
    

    public LevelEditorButton(int id, IEditable tile, int x, int y) {
        super(x, y, WIDTH, HEIGHT);
        this.id = id;
        this.tile = tile;
    }

   public IEditable getTile() {
	   return this.tile;
   }


    @Override
    public void render(AbstractScreen screen) {
        // render background
        screen.blit(Art.backLevelEditorButton[isPressed() ? 1 : (isActive ? 2 : 0)], getX(), getY());

        // scale bitmap
        AbstractBitmap smallBitmap = screen.scaleBitmap(tile.getBitMapForEditor(),
                (int) (tile.getBitMapForEditor().getWidth() / scale), (int) (tile.getBitMapForEditor().getHeight() / scale));

        // render icon 
        screen.blit(smallBitmap, getX() + (getWidth() - smallBitmap.getWidth()) / 2, getY() + (getHeight() - smallBitmap.getHeight()) / 2);
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