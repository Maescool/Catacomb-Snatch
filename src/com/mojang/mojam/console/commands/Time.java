package com.mojang.mojam.console.commands;

import java.util.Date;

import com.mojang.mojam.console.Console.Command;

public class Time extends Command {

    public Time() {
	super("time", 0, "Shows the current time");
    }

    @Override
    public void doCommand(String[] s) {
	log(new Date(System.currentTimeMillis()).toString());
    }
    
    public boolean canRunInGame() {
	return true;
    }

    public boolean canRunInMenu() {
	return true;
    }
}
