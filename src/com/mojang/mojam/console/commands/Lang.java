package com.mojang.mojam.console.commands;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.console.Console.Command;

public class Lang extends Command {

    public Lang() {
	super("lang", 1, "Sets the language");
    }

    public void doCommand(String[] args) {
	if (args[0].equals("help")) {
	    log("Enter your two letter language code, e.g. /lang af -> Afrikaans, /lang it -> Italiano");
	} else {
	    MojamComponent.instance.setLocale(args[0]);
	}
    }

}
