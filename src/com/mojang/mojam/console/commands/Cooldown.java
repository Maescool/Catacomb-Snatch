package com.mojang.mojam.console.commands;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.console.Console.Command;

public class Cooldown extends Command {

    public Cooldown() {
	super("cool", 1, "Cools the currently held weapon to a certain value");
    }

    public void doCommand(String[] s) {
	try {
	    int i = Integer.parseInt(s[0].trim());
	    log("Cooling weapon from " + i + " centispecks.");
	    for (; i > 0; i--) {
		MojamComponent.instance.player.weapon.weapontick();
	    }
	} catch (NumberFormatException e) {
	    log("Cooling weapon");
	    int i = 600;
	    for (; i > 0; i--) {
		MojamComponent.instance.player.weapon.weapontick();
	    }
	}
    }

}