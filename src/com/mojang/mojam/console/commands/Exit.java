package com.mojang.mojam.console.commands;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.console.Console.Command;

public class Exit extends Command{

    public Exit() {
	super("exit", 1, "exits the game. 0 force exit, 1 regular game exit");
    }

    public void doCommand(String[] args) {
	if(args.length > 0 && args[0].equals("0"))
		System.exit(0);
	else
		MojamComponent.instance.stop(false);
}
}
