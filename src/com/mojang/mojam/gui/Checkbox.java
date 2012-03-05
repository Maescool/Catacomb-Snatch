package com.mojang.mojam.gui;

import com.mojang.mojam.MouseButtons;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Screen;

public class Checkbox extends ClickableComponent
{
    private final int id;

    private String label;
    public boolean checked = false;

    public static final int WIDTH = 140;
    public static final int HEIGHT = 19;
    
    public Checkbox(int id, String label, int x, int y)
    {
        this(id, label, x, y, false);
    }
    
    public Checkbox(int id, String label, int x, int y, boolean checked)
    {
        super(x, y, 128, 24);
        this.id = id;
        this.label = label;
        this.checked = checked;
    }

    protected void clicked(MouseButtons mouseButtons)
    {
        checked = ! checked;
    }
    
    public void render(Screen screen)
    {
        if (isPressed()) {
            if(checked)
                screen.blit(Art.checkbox[1][1], getX(), getY());
            else
                screen.blit(Art.checkbox[0][1], getX(), getY());
        } else {
            if(checked)
                screen.blit(Art.checkbox[1][0], getX(), getY());
            else
                screen.blit(Art.checkbox[0][0], getX(), getY());
        }
        
        Font.defaultFont().draw(screen, label, getX() + 24 + 4, getY() + getHeight() / 2 - 4);
    }

    public int getId()
    {
        return id;
    }
}
