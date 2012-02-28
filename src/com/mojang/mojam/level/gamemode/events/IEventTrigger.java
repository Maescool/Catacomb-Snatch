package com.mojang.mojam.level.gamemode.events;

import com.mojang.mojam.level.Level;

public interface IEventTrigger {
	void updateTrigger(Level level);
	boolean triggerActivated();
}
