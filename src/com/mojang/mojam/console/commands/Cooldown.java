package com.mojang.mojam.console.commands;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.console.Console.Command;
import com.mojang.mojam.entity.Player;

public class Cooldown extends Command {

    Player[] players = MojamComponent.instance.players;

    public Cooldown() {
	super("cool", 1, "Cools the currently held weapon to a certain value",
		true);
    }

    public void execute() {
	try {
	    int i = Integer.parseInt(args[0].trim());
	    log("Cooling weapon from " + i + " centispecks.");
	    for (; i > 0; i--) {
		for (Player player : players) {
		    player.weapon.weapontick();
		}
	    }
	} catch (NumberFormatException e) {
	    log("Cooling weapon");
	    int i = 600;
	    for (; i > 0; i--) {
		for (Player player : players) {
		    player.weapon.weapontick();
		}
	    }
	}
    }

    public boolean canRunInGame() {
	return true;
    }

    public boolean canRunInMenu() {
	return false;
    }

}
