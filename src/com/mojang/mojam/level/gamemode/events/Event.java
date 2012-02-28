package com.mojang.mojam.level.gamemode.events;

import java.util.ArrayList;

import com.mojang.mojam.level.Level;

public class Event {
	
	Level level;
	ArrayList<IEventTrigger> triggers;
	ArrayList<IEventEffect> effects;
	
	public Event(Level level) {
		this.level = level;
		triggers = new ArrayList<IEventTrigger>();
		effects = new ArrayList<IEventEffect>();
	}
	
	public void updateEvent() {
		for (IEventTrigger trigger : triggers) {
			trigger.updateTrigger(level);
			
			if(trigger.triggerActivated())
				eventTriggered();			

			break;
		}		
	}
	
	private void eventTriggered() {
		for(IEventEffect effect : effects) {
			effect.triggerEffect(level);
		}
		level.removeEvent(this);
	}
	
	public void add(IEventTrigger trigger) {
		triggers.add(trigger);
	}
	
	public void add(IEventEffect effect) {
		effects.add(effect);
	}
}
