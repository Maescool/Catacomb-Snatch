package com.mojang.mojam.buff;

import com.mojang.mojam.buff.Buff;

class TrollPerk extends Buff {
	public BuffType buffType = BuffType.REGEN_RATE;
	private boolean infinite = false;
	
	public TrollPerk(float how) {
		super(0,how);
	}
	
	public float combine( float b ) {
		return b * ( 1 + this.param ) ;
	}
}