package com.mojang.mojam.console.commands;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.console.Console.Command;

public class Exit extends Command {

    public Exit() {
	super("exit", 1, "exits the game. 0 force exit, 1 regular game exit",
		true);
    }

    public void execute() {
	if (args.length > 0 && args[0].equals("0")) {
	    System.exit(0);
	} else {
	    MojamComponent.instance.stop(false);
	}
    }

    public boolean canRunInGame() {
	return true;
    }

    public boolean canRunInMenu() {
	return true;
    }
}
