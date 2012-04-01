package com.mojang.mojam.console.commands;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.console.Console.Command;
import com.mojang.mojam.gui.TitleMenu;

public class Menu extends Command {

    public Menu() {
	super("menu", 0, "Return to menu",true);
    }

    public void execute() {
	MojamComponent.instance.handleAction(TitleMenu.RETURN_TO_TITLESCREEN);
    }
    
    public boolean canRunInGame() {
	return true;
    }

    public boolean canRunInMenu() {
	return true;
    }

}
