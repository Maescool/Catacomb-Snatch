package com.mojang.mojam.buff;

import java.util.LinkedList;

public class Buffs extends LinkedList<Buff> {
	
	private static final long serialVersionUID = 1L;

	/* The order is FILO */
	public boolean add(Buff buff) {
		super.addFirst(buff); return true;
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
	
	/* Remove all effect who is terminated */ // <- Sorry for bad english
	public void tick() {
		this.removeOver();
	}
	
	protected void removeOver() {
		for (Buff b : this) {
			if (b.over()) {
				this.remove(b);
			}
		}
	}
	
}