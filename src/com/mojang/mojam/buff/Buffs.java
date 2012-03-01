package com.mojang.mojam.buff;

import java.util.LinkedList;
import com.mojang.mojam.buff.Buff;

public class Buffs extends LinkedList<Buff> {
	
	public float effectsOf(Buff.BuffType buffType, float val ) {
		for (Buff b : this) {
			if (b.is(buffType)) {
				val = b.combine( val );
			}
		}
		return val;
	}
	
	public void tick() {
		this.tickTime();
		this.removeOver();
	}
	
	protected void tickTime() {
		for (Buff b : this) {
			b.tick();
		}
	}
	
	protected void removeOver() {
		for (Buff b : this) {
			if (b.over()) {
				this.remove(b);
			}
		}
	}
	
}