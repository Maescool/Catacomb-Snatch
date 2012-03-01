package com.mojang.mojam.buff;

abstract public class Buff {
	/**
	 * Type of buff
	 */
	public enum BuffType { 
		ALL, HEALTH_MODIF, REGEN_RATE, EXP_MODIF , EXP_RATE
	}
	
	public BuffType buffType;
	
	protected boolean dispell  = true;
	protected boolean infinite = false;
	protected int duration;
	protected float param;
	
	public Buff(int duration, float how) {
		this.duration = duration;
		this.param = how;
	}
	
	public float value() {
		if (this.infinite || this.duration > 0) {
			return this.param;
		} else {
			return 0;
		}
	}
	
	public float combine( float b ) {
		return this.param + b ;
	}
	
	public void tick() { this.duration-- ; }
	
	public boolean is(BuffType type) { return type == BuffType.ALL || this.buffType == type ; }
	public boolean over() { return !this.infinite || this.duration <= 0 ;}
	public boolean canDispell() { return this.dispell ; }
}