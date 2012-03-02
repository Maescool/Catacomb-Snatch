package com.mojang.mojam.buff;

import com.mojang.mojam.buff.Buff;

public class TrollPerk extends Buff {
	public BuffType buffType = BuffType.REGEN_RATE;
	protected boolean dispell  = false;
	protected boolean infinite = false;
	
	public TrollPerk(float how) {
		super(0,how);
	}
	
	public float combine( float b ) {
		return b * ( 1 + this.param ) ;
	}
}