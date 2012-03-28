package com.mojang.mojam.console.commands;

import com.mojang.mojam.console.Console;
import com.mojang.mojam.console.Console.Command;

public class Help extends Console.Command {

    public Help() {
	super("help", 0, "Displays all possible commands");
    }

    public void doCommand(String[] args) {
	log("All Commands");
	log("--------------");
	for (int i = 0; i < Command.commands.size(); i++) {
	    Command c = Command.commands.get(i);
	    if (c != null)
		log(c.name + " : " + c.helpMessage);
	}
    }
    
    public boolean canRunInGame() {
	return true;
    }

    public boolean canRunInMenu() {
	return true;
    }

}
