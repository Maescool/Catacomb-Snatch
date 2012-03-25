package com.mojang.mojam.console.commands;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.console.Console.Command;

public class Chat extends Command{

    public Chat() {
	super("chat", -1, "Does the same as pressing T and typing in the after /chat and pressing enter");
    }

    public void doCommand(String[] args) {
	String msg = "";
	for(int i = 0; i < args.length-1; i++) {
		msg += args[i] + " ";
	}
	msg += args[args.length-1];
	MojamComponent.instance.synchronizer.addCommand(new com.mojang.mojam.network.packet.ChatCommand(msg));
}
    
}
