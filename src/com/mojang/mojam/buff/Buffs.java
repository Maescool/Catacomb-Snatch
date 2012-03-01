package com.mojang.mojam.buff;

import java.util.LinkedList;
import com.mojang.mojam.buff.Buff;

public class Buffs extends LinkedList<Buff> {
	
	/* The order is FILO */
	public boolean add(Buff buff) {
		return super.addFirst(buff);
	}
	
	/* Return combined value of all buff of "buffType" type */
	public float effectsOf(Buff.BuffType buffType, float val ) {
		for (Buff b : this) {
			if (b.is(buffType)) {
				val = b.combine( val );
			}
		}
		return val;
	}
	
	/* Remove all dispell-able effects of one type (Use BuffType.ALL for all type)*/
	public void dispell() {
		for (Buff b : this) {
			if (b.canDispell()) {
				this.remove(b);
			}
		}
	}
	
	/* First -- to duration, second remove all effect who is terminated */ // <- Sorry for bad english
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