package com.mojang.mojam.console.commands;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.console.Console.Command;
import com.mojang.mojam.network.kryo.Network.PauseMessage;

public class Pause extends Command{

    public Pause() {
	super("pause", 0, "Pauses the game");
    }

    public void doCommand(String[] args) {
	MojamComponent.instance.console.close();
	MojamComponent.instance.synchronizer.addMessage(new PauseMessage(true));
    }

}