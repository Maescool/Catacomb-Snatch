package com.mojang.mojam.console.commands;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.console.Console.Command;
import com.mojang.mojam.network.kryo.Network.PauseMessage;

public class Pause extends Command{

    public Pause() {
	super("pause", 0, "Pauses the game",true);
    }

    public void execute() {
	MojamComponent.instance.console.toggle();
	MojamComponent.instance.synchronizer.addMessage(new PauseMessage(true));
    }
    
    public boolean canRunInGame() {
	return true;
    }

    public boolean canRunInMenu() {
	return false;
    }

}
