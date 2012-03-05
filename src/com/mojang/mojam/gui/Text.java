package com.mojang.mojam.gui;

import com.mojang.mojam.screen.Screen;

public class Text extends VisibleComponent
{
    private final int id;

    private String label;
    
    public Text(int id, String label, int x, int y)
    {
        super(x, y, 512, 24);
        this.id = id;
        this.label = label;
    }
    
    public void render(Screen screen)
    {
    	if (label.indexOf("\n") != -1) {
    		String[] strings = label.split("\n");
			Integer amount = 0;
			for (String text: strings) {
				Font.defaultFont().draw(screen, text, getX() + 24 + 4, (getY() + getHeight() / 2 - 4) + amount * 20, 512);
				++amount;
			}
    	} else {
    		Font.defaultFont().draw(screen, label, getX() + 24 + 4, getY() + getHeight() / 2 - 4, 512);
    	}
    }

    public int getId()
    {
        return id;
    }
}
