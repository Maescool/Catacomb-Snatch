package com.mojang.mojam.console.commands;

import com.mojang.mojam.console.Console.Command;

public class Load extends Command {

    public Load() {
	super("load", 1, "Loads a map by name");
    }

    @Override
    public void doCommand(String[] args) {
	log("Loading map " + args[0]);
	log("Incomplete");
	// TitleMenu.level = new LevelInformation(args[0], "/levels/" +
	// args[0].replace('_', ' ') + ".bmp", true);
	// TODO: Needs MUCH work
	// MojamComponent.instance.handleAction(TitleMenu.START_GAME_ID);
    }
    
    public boolean canRunInGame() {
	return true;
    }

    public boolean canRunInMenu() {
	return true;
    }

}
